package com.heroslender.hmf.intellij.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.currentComposer
import com.heroslender.hmf.core.Preview
import com.heroslender.hmf.intellij.preview.components.MenuComponent
import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.OrderEnumerator
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.lang.UrlClassLoader
import org.jetbrains.kotlin.idea.base.util.module
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import java.io.File
import java.net.MalformedURLException
import java.nio.file.Path

typealias ComposableFunc = @Composable () -> Unit

@OptIn(ExperimentalComposeApi::class)
fun invokePreviewMethod(clazz: Class<*>, methodName: String): Pair<Preview, ComposableFunc> {
    val method = ComposableInvoker.getComposableMethod(clazz, methodName)
    val preview = method.getDeclaredAnnotation(Preview::class.java)
    if (preview == null) {
        throw IllegalStateException("Method has no preview annotation")
    }

    return preview to @Composable {
        ComposableInvoker.invokeComposable(clazz, method, currentComposer)
    }
}

fun invokePreview(function: KtNamedFunction): Pair<Preview, ComposableFunc> {
    val className: String
    val functionParent = function.parent
    if (functionParent is KtFile) {
        className = functionParent.packageFqName.asString() + '.' + functionParent.name.replace(".kt", "Kt")
    } else {
        className = function.containingClass()?.fqName?.asString()
            ?: throw IllegalArgumentException("Unsupported preview class format")
    }
    val module = function.module!!
    val loader = getClassLoader(module)
    val clazz = Class.forName(className, true, loader)

    val preview =
        // We need to load the MenuComponent as part of the same ClassLoader to prevent class cast issues
        Class.forName("com.heroslender.hmf.intellij.preview.PreviewInvokerKt", true, loader).let { c ->
            c.getMethod("invokePreviewMethod", Class::class.java, String::class.java)
                .invoke(null, clazz, function.name) as? Pair<Preview, ComposableFunc>
        }

    return preview ?: throw IllegalStateException("Failed to load preview")
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