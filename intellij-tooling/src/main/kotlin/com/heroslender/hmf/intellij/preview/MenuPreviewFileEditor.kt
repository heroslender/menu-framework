package com.heroslender.hmf.intellij.preview

import com.heroslender.hmf.intellij.preview.components.LayeredPaneLayout
import com.heroslender.hmf.intellij.preview.components.MenuComponent
import com.heroslender.hmf.intellij.preview.components.MenuListComponent
import com.heroslender.hmf.intellij.preview.components.ToolbarComponent
import com.intellij.icons.AllIcons
import com.intellij.openapi.fileEditor.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.forEachDescendantOfType
import com.intellij.task.ProjectTaskManager
import com.intellij.ui.AncestorListenerAdapter
import com.intellij.util.messages.MessageBusConnection
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.beans.PropertyChangeListener
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.event.AncestorEvent


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

        addMouseWheelListener {
            // Just to fix an issue that would cause the resized components to reset
        }

        addAncestorListener(object : AncestorListenerAdapter() {
            override fun ancestorMoved(event: AncestorEvent) {
                redraw()
            }
        })
    }
    private var menusPanel = MenuListComponent(myUi, MenuListComponent.Options())

    private var messageBus: MessageBusConnection? = null

    private fun setup() {
        reCompose()
        redraw()

        val bus = myProject.messageBus.connect()
        this.messageBus?.disconnect()
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

    private var lastRebuild = System.currentTimeMillis()
    private fun rebuild() {
        val now = System.currentTimeMillis()
        if (now - lastRebuild < 100) {
            return
        }
        lastRebuild = now

        if (inProgress) {
            shouldRebuild = true
            return
        }

        inProgress = true
        val start = System.currentTimeMillis()
        ProjectTaskManager.getInstance(myProject).buildAllModules().onSuccess {
            reCompose()
            redraw()
            println("Updated preview in ${System.currentTimeMillis() - start}ms.")

            inProgress = false
            if (shouldRebuild) {
                shouldRebuild = false
                rebuild()
            }
        }
    }

    private fun JComponent.bindToLeft(): JPanel {
        return JPanel().apply {
            isVisible = true
            layout = BoxLayout(this, BoxLayout.X_AXIS)

            add(this@bindToLeft)
        }
    }

    private data class MenuData(
        val labelText: String,
        val menuComponent: MenuComponent,
    )

    private val data = mutableListOf<MenuData>()

    private fun reCompose() {
        data.clear()
        val tree = PsiDocumentManager.getInstance(myProject).getPsiFile(myDocument) ?: return
        tree.forEachDescendantOfType<KtNamedFunction> { function ->
            val annotations = function.annotationEntries
            if (annotations.isEmpty()) {
                return@forEachDescendantOfType
            }

            if (!annotations.any { it.resolveToDescriptorIfAny(BodyResolveMode.FULL)?.fqName?.asString() == PREVIEW_QUALIFIED_NAME }) {
                return@forEachDescendantOfType
            }

            val menuComponent = invokePreview(function)
            if (menuComponent != null) {
                data.add(MenuData(
                    function.name + " has @Preview! (${function.fqName?.asString()})",
                    menuComponent
                ))
            }
        }
    }

    private fun redraw() {
        if (!myUi.isShowing) return

        // clear the old UI
        myUi.removeAll()

        if (data.isNotEmpty()) {
            val opts = menusPanel.opts
            menusPanel = MenuListComponent(myUi, opts)

            for ((labelText, menuComponent) in data) {
                menusPanel.add(
                    JPanel().apply {
                        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                        border = EmptyBorder(40, 0, 10, 0) // Margin around the element

                        add(JLabel(labelText))
                    }
                )

                menusPanel.add(menuComponent)
            }

            val toolbar = ToolbarComponent().apply {
                addButton(AllIcons.General.Add) {
                    menusPanel.opts.zoomFactor *= 1.1
                    myUi.repaint()
                }
                addButton(AllIcons.General.Remove) {
                    menusPanel.opts.zoomFactor /= 1.1
                    myUi.repaint()
                }
                addButton(AllIcons.General.ActualZoom) {
                    menusPanel.opts.apply {
                        zoomFactor = 1.0
                        xOffset = 0.0
                        yOffset = 0.0
                        xDiff = 0
                        yDiff = 0
                        dragger = true
                        released = true
                    }
                    myUi.repaint()
                }
            }

            val panel = JPanel().apply {
                layout = FlowLayout(FlowLayout.CENTER, 0, 0)
                add(toolbar)
                setBounds(
                    myUi.width - (this.preferredSize.width + 20),
                    myUi.height - (this.preferredSize.height + 50),
                    this.preferredSize.width,
                    this.preferredSize.height
                )
            }

            myUi.setLayer(menusPanel, 0)
            myUi.add(menusPanel)

            myUi.setLayer(panel, 1)
            myUi.add(panel)
        } else {

            myUi.add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                border = EmptyBorder(10, 10, 0, 0) // Margin around the element

                add(
                    JLabel("Currently this source code has no menu declaration!").bindToLeft().apply {
                        border = EmptyBorder(0, 0, 5, 0)
                    },
                    BorderLayout.CENTER
                )

                add(JButton("Rebuild").apply {
                    addActionListener {
                        rebuild()
                    }
                })
            })
        }
    }


    override fun isModified(): Boolean = false

    override fun isValid(): Boolean = true

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}

    override fun getName(): String = "HMF Menu Viewer"

    override fun setState(state: FileEditorState) {}

    override fun getComponent(): JComponent = myUi

    override fun getPreferredFocusedComponent(): JComponent? = null

    override fun getCurrentLocation(): FileEditorLocation? = null

    override fun selectNotify() {
        setup()
    }

    override fun deselectNotify() {
        dispose()
    }

    override fun dispose() {
        messageBus?.disconnect()
        messageBus = null
    }
}