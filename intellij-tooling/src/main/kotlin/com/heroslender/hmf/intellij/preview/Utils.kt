package com.heroslender.hmf.intellij.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.currentComposer
import com.heroslender.hmf.core.Menu
import com.heroslender.hmf.core.Preview
import com.heroslender.hmf.core.compose.ComposeMenu
import com.heroslender.hmf.core.compose.LocalCanvas
import com.heroslender.hmf.core.compose.LocalImageProvider
import com.heroslender.hmf.core.compose.LocalMenu
import com.heroslender.hmf.core.ui.components.Box
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.modifiers.maxSize
import com.heroslender.hmf.intellij.insight.PreviewLineMarkerProvider.Companion.COMPOSABLE_QUALIFIED_NAME
import com.heroslender.hmf.intellij.preview.components.MenuComponent
import com.heroslender.hmf.intellij.preview.impl.PreviewCanvas
import com.heroslender.hmf.intellij.preview.impl.PreviewMenuManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.lang.UrlClassLoader
import org.jetbrains.kotlin.idea.base.util.module
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import java.io.File
import java.lang.reflect.Method
import java.net.MalformedURLException
import java.nio.file.Path
import javax.swing.JComponent

@Suppress("unused")
fun drawPreview(method: Method, objInstance: Any?): MenuComponent {
    val preview = method.getDeclaredAnnotation(Preview::class.java)
    val classLoader = method.declaringClass.classLoader
    val manager = PreviewMenuManager(classLoader)
    val canvas = PreviewCanvas(preview.width, preview.height)

    val composeMenu = ComposeMenu().apply {
        rootNode.canvas = canvas
        start {
            CompositionLocalProvider(
                LocalCanvas provides canvas,
                LocalImageProvider provides manager.imageProvider,
                LocalMenu provides object : Menu {
                    @Composable
                    override fun getUi() {
                    }

                    override fun close() {
                    }
                },
            ) {
                Box(modifier = Modifier.maxSize(canvas.width, canvas.height)) {
                    method.invoke(objInstance, currentComposer, 8)
                }
            }

        }
    }

    var name = preview.name
    if (name.isEmpty()) {
        name = method.name
    }
    return MenuComponent(name, composeMenu)
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
    val aClass: Class<*> = Class.forName(className, true, loader)
    val clazz = Class.forName(COMPOSABLE_QUALIFIED_NAME, true, loader)
    val f = Class.forName("com.heroslender.hmf.core.font.FontsKt", true, loader)
    val method = aClass.getMethod(function.name!!, clazz, Int::class.java)
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
    val files: MutableList<Path> = ArrayList()
    val list: List<String> = OrderEnumerator.orderEntries(module).recursively().runtimeOnly().pathsList.pathList
    for (path in list) {
        try {
            files.add(File(FileUtil.toSystemIndependentName(path)).toPath())
        } catch (e1: MalformedURLException) {
            e1.printStackTrace()
        }
    }

    return UrlClassLoader.build().files(files).parent(MenuComponent::class.java.classLoader).get()
}