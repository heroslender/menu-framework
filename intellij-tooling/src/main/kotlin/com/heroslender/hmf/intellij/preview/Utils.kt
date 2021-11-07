package com.heroslender.hmf.intellij.preview

import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.ui.ComposableNode
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.modifiers.background
import com.heroslender.hmf.core.ui.modifier.modifiers.maxSize
import com.heroslender.hmf.intellij.preview.components.MenuComponent
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.util.lang.UrlClassLoader
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.idea.util.module
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import java.awt.Color
import java.io.File
import java.lang.reflect.Method
import java.net.MalformedURLException
import java.net.URL
import javax.swing.JComponent

fun VirtualFile.hasPreview(project: Project): Boolean {
    return toPsiFile(project)?.hasPreview() ?: false
}

fun PsiFile.hasPreview(): Boolean {
    return anyDescendantOfType<KtNamedFunction> { function ->
        val annotations = function.annotationEntries
        if (annotations.isEmpty()) {
            return@anyDescendantOfType false
        }

        if (!annotations.any { it.resolveToDescriptorIfAny(BodyResolveMode.FULL)?.fqName?.asString() == MenuPreviewFileEditor.PREVIEW_QUALIFIED_NAME }) {
            return@anyDescendantOfType false
        }

        return@anyDescendantOfType true
    }
}

fun drawPreview(method: Method, objInstance: Any?): MenuComponent {
    val context = Context(Context.ICanvas(512, 380), classLoader = method.declaringClass.classLoader)

    val root = ComposableNode(
        parent = null,
        modifier = Modifier.maxSize(context.canvas.width, context.canvas.height).background(object : IColor {
            override val id: Byte = 50
            override val color: Color = Color.BLUE
            override val isTransparent: Boolean = false
        }),
        renderContext = context
    ) {
        try {
            method.invoke(objInstance, this)
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
        }
    }
    context.root = root

    root.compose()
    root.measure(Constraints())

    root.outerWrapper.placeAt(0, 0)

    root.draw(context.canvas)

    return MenuComponent(root)
}

fun invokePreview(function: KtNamedFunction): MenuComponent? {
    val isTopLevel: Boolean
    val className: String
    val functionParent = function.parent
    if (functionParent is KtFile) {
        isTopLevel = true
        className = functionParent.packageFqName.asString() + '.' + functionParent.name.replace(".kt", "Kt")
    } else {
        isTopLevel = false
        className = function.containingClass()?.fqName?.asString() ?: return null
    }

    val module = function.module!!
    val loader = getClassLoader(module)
    val aClass: Class<*> = try {
        Class.forName(className, true, loader)
    } catch (e: ClassNotFoundException) {
        println("Cannot find class '$className'")
        return null
    }

    val method = try {
        aClass.getMethod(function.name!!, Class.forName(MenuPreviewFileEditor.COMPOSABLE_QUALIFIED_NAME, true, loader))
    } catch (e: NoSuchMethodException) {
        return null
    }

    val instance = if (isTopLevel) {
        null
    } else {
        aClass.getConstructor().newInstance()
    }

    val menuComponent =
        // We need to load the MenuComponent as part of the same ClassLoader to prevent class cast issues
        Class.forName("com.heroslender.hmf.intellij.preview.UtilsKt", true, loader).let { c ->
            c.getMethod("drawPreview", Method::class.java, Any::class.java)
                .invoke(null, method, instance) as? JComponent
        }

    return menuComponent as? MenuComponent
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