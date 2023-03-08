package com.heroslender.hmf.intellij.insight

import com.intellij.codeInsight.daemon.*
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiEditorUtil
import com.intellij.util.FunctionUtil
import com.intellij.util.ui.ColorIcon
import com.intellij.util.ui.ColorsIcon
import java.awt.Color
import javax.swing.Icon

class ColorLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        val info = element.findColor { chosen -> ColorInfo(element, chosen.color) }
        if (info != null) {
            NavigateAction.setNavigateAction(info, "Change color", null)
        }

        return info
    }

    open class ColorInfo(element: PsiElement, protected val color: Color) : MergeableLineMarkerInfo<PsiElement>(
        element,
        element.textRange,
        ColorIcon(12, color),
        FunctionUtil.nullConstant<Any, String>(),
        GutterIconNavigationHandler handler@{ _, psiElement ->
            if (!psiElement.isWritable || !element.isValid) {
                return@handler
            }

            val editor = PsiEditorUtil.findEditor(element) ?: return@handler

            println("Clicked on the color!")
            val picker = ColorPicker(editor.component)
            val newColor = picker.showDialog()
            if (newColor != null) {
                element.setColor(newColor)
            }
        },
        GutterIconRenderer.Alignment.RIGHT
    ) {

        override fun canMergeWith(info: MergeableLineMarkerInfo<*>) = info is ColorInfo

        override fun getCommonIconAlignment(infos: List<MergeableLineMarkerInfo<*>>) =
            GutterIconRenderer.Alignment.RIGHT

        override fun getCommonIcon(infos: List<MergeableLineMarkerInfo<*>>): Icon {
            if (infos.size == 2 && infos[0] is ColorInfo && infos[1] is ColorInfo) {
                return ColorsIcon(12, (infos[0] as ColorInfo).color, (infos[1] as ColorInfo).color)
            }
            return AllIcons.Gutter.Colors
        }

        override fun getCommonTooltip(infos: List<MergeableLineMarkerInfo<*>>) =
            FunctionUtil.nullConstant<PsiElement, String>()
    }
}