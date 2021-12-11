package com.heroslender.hmf.intellij.insight

import com.heroslender.hmf.intellij.preview.components.MenuPreviewComponent
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.RegisterToolWindowTask
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.ui.content.ContentFactory
import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode

class PreviewLineMarkerProvider : LineMarkerProvider {
    companion object {
        const val PREVIEW_QUALIFIED_NAME: String = "com.heroslender.hmf.core.Preview"
        const val COMPOSABLE_QUALIFIED_NAME: String = "com.heroslender.hmf.core.ui.Composable"
        const val ID = "HMF_PREVIEW"
    }

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        val (leaf, function) = element.findPreview() ?: return null
        val info = LineMarkerInfo(
            leaf,
            leaf.textRange,
            AllIcons.Actions.RunAll,
            {
                "Execute preview"
            },
            GutterIconNavigationHandler handler@{ _, psiElement ->
                if (!psiElement.isWritable || !element.isValid) {
                    return@handler
                }

                val manager = ToolWindowManager.getInstance(element.project)
                val toolWindow = manager.getToolWindow(ID) ?: manager.registerToolWindow(
                    RegisterToolWindowTask(
                        id = ID,
                        anchor = ToolWindowAnchor.RIGHT,
                        icon = MenuPreviewComponent.PreviewIcon
                    )
                )

                toolWindow.setIcon(IconLoader.getIcon("/hmf-icons/layoutPreviewOnly.svg",
                    PreviewLineMarkerProvider::class.java))
                toolWindow.stripeTitle = "Menu Preview"
                toolWindow.isShowStripeButton = true
                val contentManager = toolWindow.contentManager
                val content = contentManager.findContent(function.name)
                    ?: ContentFactory.SERVICE.getInstance().createContent(
                        MenuPreviewComponent(element.project, function, toolWindow),
                        function.name,
                        true
                    ).apply {
                        (component as MenuPreviewComponent).content = this
                        icon = MenuPreviewComponent.PreviewIcon
                        putUserData(ToolWindow.SHOW_CONTENT_ICON, true)
                        contentManager.addContent(this)
                    }

                toolWindow.activate {
                    contentManager.setSelectedContent(content)
                    val component = content.component as MenuPreviewComponent
                    component.rebuildTask.run()
                }
            },
            GutterIconRenderer.Alignment.RIGHT,
            { "Preview" }
        )

        return info
    }

    inline fun PsiElement.findPreview(): Pair<LeafPsiElement, KtNamedFunction>? {
        if (this !is LeafPsiElement || elementType.toString() != "fun") {
            return null
        }

        val func = parent as? KtNamedFunction ?: return null

        val annotations = func.annotationEntries
        if (annotations.isEmpty()) {
            return null
        }

        if (!annotations.any { it.resolveToDescriptorIfAny(BodyResolveMode.FULL)?.fqName?.asString() == PREVIEW_QUALIFIED_NAME }) {
            return null
        }

        return this to func
    }
}