package com.heroslender.hmf.intellij.insight

import com.heroslender.hmf.intellij.preview.components.MenuPreviewComponent
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.RegisterToolWindowTask
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.ui.content.*
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
            { "Execute preview" },
            GutterIconNavigationHandler handler@{ _, psiElement ->
                try {
                    if (!psiElement.isWritable || !psiElement.isValid) {
                        return@handler
                    }

                    val previewId = function.containingFile.name + '#' + function.name
                    val toolWindow = getOrCreateToolWindow(psiElement.project)
                    val contentManager = toolWindow.contentManager
                    val content = contentManager.findContentMenuById(previewId)
                        ?: createContent(previewId, psiElement.project, toolWindow, function)

                    toolWindow.isAvailable = true
                    toolWindow.activate {
                        contentManager.setSelectedContent(content)
                        val component = content.component as MenuPreviewComponent
                        component.rebuildTask.run()
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            },
            GutterIconRenderer.Alignment.RIGHT,
            { "Preview" }
        )

        return info
    }

    private fun createContent(
        previewId: String,
        project: Project,
        toolWindow: ToolWindow,
        function: KtNamedFunction,
    ): Content {
        val previewComponent = MenuPreviewComponent(previewId, project, function, toolWindow)
        val content = ContentFactory.SERVICE.getInstance().createContent(previewComponent, function.name, true)
        previewComponent.content = content
        previewComponent.menuComponent?.menuName?.also { content.displayName = it }
        content.icon = MenuPreviewComponent.PreviewIcon
        content.putUserData(ToolWindow.SHOW_CONTENT_ICON, true)
        toolWindow.contentManager.addContent(content)

        return content
    }

    private fun ContentManager.findContentMenuById(id: String): Content? {
        for (content in contents) {
            val component = content.component as? MenuPreviewComponent ?: continue
            if (component.menuPreviewId == id) {
                return content
            }
        }

        return null
    }

    private fun getOrCreateToolWindow(project: Project): ToolWindow {
        val manager = ToolWindowManager.getInstance(project)

        var toolWindow = manager.getToolWindow(ID)
        if (toolWindow == null) {
            toolWindow = manager.registerToolWindow(
                RegisterToolWindowTask(
                    id = ID,
                    anchor = ToolWindowAnchor.RIGHT,
                    icon = MenuPreviewComponent.PreviewIcon,
                    stripeTitle = { "Menu Preview" },
                )
            )

            toolWindow.addContentManagerListener(object : ContentManagerListener {
                override fun contentRemoved(content: ContentManagerEvent) {
                    if (toolWindow.contentManagerIfCreated?.contents?.isEmpty() == true && toolWindow.isVisible) {
                        toolWindow.isAvailable = false
//                        ToolWindowManager.getInstance(project).unregisterToolWindow(ID)
                    }
                }
            })
        }

        return toolWindow
    }

    private fun PsiElement.findPreview(): Pair<LeafPsiElement, KtNamedFunction>? {
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