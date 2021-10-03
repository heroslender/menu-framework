package com.heroslender.hmf.intellij.preview

import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.forEachDescendantOfType
import com.intellij.task.ProjectTaskManager
import com.intellij.util.lang.UrlClassLoader
import com.intellij.util.messages.MessageBusConnection
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.util.module
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import java.awt.*
import java.beans.PropertyChangeListener
import java.io.File
import java.lang.reflect.Method
import java.net.MalformedURLException
import java.net.URL
import javax.swing.*
import javax.swing.border.EmptyBorder
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance


class MenuPreviewFileEditor(
    private val myProject: Project,
    private val myFile: VirtualFile,
    private val mainEditor: TextEditor,
) : UserDataHolderBase(), FileEditor {

    companion object {
        const val PREVIEW_QUALIFIED_NAME: String = "com.heroslender.hmf.core.Preview"
        const val COMPOSABLE_QUALIFIED_NAME: String = "com.heroslender.hmf.core.ui.Composable"
    }

    private val myDocument = FileDocumentManager.getInstance().getDocument(myFile)!!
    private val myUi = JLayeredPane().apply {
        layout = LayeredPaneLayout(this)
        isVisible = true
        minimumSize = Dimension(
            50,
            50
        )
    }

    private var messageBus: MessageBusConnection? = null

    fun setup() {
        println("Setting up editor!")
        redraw()

        val bus = myProject.messageBus.connect()
        this.messageBus = bus
        bus.subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
            override fun after(events: List<VFileEvent?>) {
                if (!events.any { it?.file == myFile }) return

                rebuild()
            }
        })
    }

    private var inProgress = false
    private var shouldRebuild = false

    private fun rebuild() {
        if (inProgress) {
            shouldRebuild = true
            return
        }

        inProgress = true

        val start = System.currentTimeMillis()
        ProjectTaskManager.getInstance(myProject).buildAllModules().onSuccess {
            redraw()
            println("Updated in ${System.currentTimeMillis() - start}ms.")

            inProgress = false
            if (shouldRebuild) {
                shouldRebuild = false
                rebuild()
            }
        }
    }

    private fun JComponent.centerComponent(): JPanel {
        return JPanel().apply {
            isVisible = true
            layout = BoxLayout(this, BoxLayout.X_AXIS)

            add(this@centerComponent)
        }
    }

    private fun redraw() {
        if (!myUi.isShowing) return

        val tree = PsiDocumentManager.getInstance(myProject).getPsiFile(myDocument) ?: return

        // clear the old UI
        myUi.removeAll()

        // create the scroll pane with a centralized component with the inventories
        var menuCount = 0
        val menusPanel = JPanel().apply {
            preferredSize = myUi.preferredSize
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
        }

        tree.forEachDescendantOfType<KtNamedFunction> { function ->
            val annotations = function.annotationEntries
            if (annotations.isEmpty()) {
                return@forEachDescendantOfType
            }

            if (!annotations.any { it.resolveToDescriptorIfAny(BodyResolveMode.FULL)?.fqName?.asString() == PREVIEW_QUALIFIED_NAME }) {
                return@forEachDescendantOfType
            }

            menusPanel.add(
                JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.LINE_AXIS)

                    // Margin around the element
                    border = EmptyBorder(40, 0, 10, 0)

                    add(JLabel(function.name + " has @Preview! (${function.fqName?.asString()})"))
                }
            )

            val isTopLevel: Boolean
            val className: String
            if (function.parent is KtFile) {
                val parent = function.parent as KtFile
                isTopLevel = true
                className = parent.packageFqName.asString() + '.' + parent.name.replace(".kt", "Kt")
            } else {
                isTopLevel = false
                className = function.containingClass()?.fqName?.asString() ?: return@forEachDescendantOfType
            }

            val module = function.module!!
            val loader = getClassLoader(module)
            val aClass: Class<*> = try {
                Class.forName(className, true, loader)
            } catch (e: ClassNotFoundException) {
                println("Cannot find class '$className'")
                return@forEachDescendantOfType
            }

            val args = arrayOf(
                Class.forName(COMPOSABLE_QUALIFIED_NAME, true, loader),
                *function.typeParameters.mapNotNull { param ->
                    param.fqName?.asString()?.let { fq -> Class.forName(fq, true, loader) }
                }.toTypedArray()
            )
            val method = try {
                aClass.getMethod(function.name!!, *args)
            } catch (e: NoSuchMethodException) {
                return@forEachDescendantOfType
            }

            val instance = if (isTopLevel) {
                null
            } else {
                aClass.getConstructor().newInstance()
            }

            val menuComponent =
                // We need to load the MenuComponent as part of the same ClassLoader to prevent class cast issues
                Class.forName("com.heroslender.hmf.intellij.preview.MenuComponent", true, loader).let { c ->
                    val m = c.kotlin.companionObject!!.java.getMethod(
                        "drawPreview",
                        Method::class.java, Any::class.java
                    )

                    m.invoke(c.kotlin.companionObjectInstance, method, instance) as? JComponent
                }

            if (menuComponent != null) {
                menusPanel.add(menuComponent)
                menuCount++
            }
        }

        if (menuCount > 0) {
            myUi.add(menusPanel)
        } else {
            myUi.add(
                JLabel("Currently this source code has no menu declaration!").centerComponent(),
                BorderLayout.CENTER
            )
        }
    }

    class LayeredPaneLayout(private val target: Container) : LayoutManager {
        override fun addLayoutComponent(name: String?, comp: Component?) {}
        override fun layoutContainer(container: Container) {
            for (component in container.components) {
                component.bounds = Rectangle(
                    component.x,
                    component.y,
                    component.preferredSize.width,
                    component.preferredSize.height
                )
            }
        }

        override fun minimumLayoutSize(parent: Container?): Dimension {
            return preferredLayoutSize(parent)
        }

        override fun preferredLayoutSize(parent: Container?): Dimension {
            return Dimension(target.width, target.height)
        }

        override fun removeLayoutComponent(comp: Component?) {}
    }

    private fun getClassLoader(module: Module): ClassLoader {
        val urls: MutableList<URL> = ArrayList()
        val list: List<String> = OrderEnumerator.orderEntries(module).recursively().runtimeOnly().pathsList.pathList
        for (path in list) {
            try {
                urls.add(File(FileUtil.toSystemIndependentName(path)).toURI().toURL())
            } catch (e1: MalformedURLException) {
                e1.printStackTrace()
            }
        }

        return UrlClassLoader.build().urls(urls).parent(MenuComponent::class.java.classLoader).get()
    }

    override fun isModified(): Boolean = false

    override fun isValid(): Boolean = true

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {

    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
    }

    override fun getName(): String = "HMF Menu Viewer"

    override fun setState(state: FileEditorState) {

    }

    override fun getComponent(): JComponent {
        return myUi
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return null
    }

    override fun getCurrentLocation(): FileEditorLocation? {
        return null
    }

    override fun selectNotify() {
        setup()
    }

    override fun deselectNotify() {
        dispose()
    }

    override fun dispose() {
        messageBus?.disconnect()
    }
}