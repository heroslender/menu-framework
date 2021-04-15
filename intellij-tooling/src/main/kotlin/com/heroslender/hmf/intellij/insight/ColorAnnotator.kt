package com.heroslender.hmf.intellij.insight

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import java.awt.Font

class ColorAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val color = element.findColorExprElement() ?: return

        setColorAnnotator(
            color.color,
            element,
            holder
        )
    }

    private fun setColorAnnotator(color: java.awt.Color, element: PsiElement, holder: AnnotationHolder) {
        val textAttributes =
            TextAttributes(null, null, color, EffectType.BOLD_LINE_UNDERSCORE, Font.PLAIN)
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element)
            .enforcedTextAttributes(textAttributes)
            .create()
    }
}
