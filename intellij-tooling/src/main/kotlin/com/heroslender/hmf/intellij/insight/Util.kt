package com.heroslender.hmf.intellij.insight

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtQualifiedExpression

const val COLOR_QUALIFIED_NAME: String = "com.heroslender.hmf.bukkit.map.Color"

fun PsiElement.findColor(): Color? = findColor { it }

inline fun <T> PsiElement.findColor(function: (Color) -> T): T? {
    if (this !is LeafPsiElement) {
        return null
    }

    val parent = parent.parent
    if (parent !is KtQualifiedExpression || parent.receiverExpression.isNotColor() || this.text == "Color") {
        return null
    }

    val colorName = parent.selectorExpression?.text ?: return null
    return function(Color.valueOf(colorName))
}

fun PsiElement.findColorExprElement(): Color? = findColorExprElement { it }

inline fun <T> PsiElement.findColorExprElement(function: (Color) -> T): T? {
    if (this !is KtQualifiedExpression || receiverExpression.isNotColor()) {
        return null
    }

    val colorName = selectorExpression?.text ?: return null

    return function(Color.valueOf(colorName))
}

fun KtExpression.isNotColor(): Boolean = mainReference?.resolve()?.getKotlinFqName()?.asString() != COLOR_QUALIFIED_NAME

fun PsiElement.setColor(color: String) {
    this.containingFile.runWriteAction {
        val split = color.split(".").dropLastWhile(String::isEmpty).toTypedArray()
        val newColorBase = split.last()

        val psiFactory = KtPsiFactory(project)
        val identifier = psiFactory.createIdentifier(newColorBase)

        this.replace(identifier)
    }
}

inline fun <T : Any?> PsiFile.runWriteAction(crossinline func: () -> T) =
    applyWriteAction { func() }

inline fun <T : Any?> PsiFile.applyWriteAction(crossinline func: PsiFile.() -> T): T {
    val result = WriteCommandAction.writeCommandAction(this).withGlobalUndo().compute<T, Throwable> { func() }
    PsiDocumentManager.getInstance(project)
        .doPostponedOperationsAndUnblockDocument(
            FileDocumentManager.getInstance().getDocument(this.virtualFile) ?: return result
        )
    return result
}
