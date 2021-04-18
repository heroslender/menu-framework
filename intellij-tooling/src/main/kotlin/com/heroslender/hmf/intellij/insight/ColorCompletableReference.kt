package com.heroslender.hmf.intellij.insight

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ui.ColorIcon
import org.jetbrains.kotlin.psi.KtQualifiedExpression


class ColorCompletableReference : CompletionContributor() {
    override fun fillCompletionVariants(parameters: CompletionParameters, resultSet: CompletionResultSet) {
        val parent = parameters.position.context?.context ?: return
        if (parent !is KtQualifiedExpression || parent.receiverExpression.isNotColor() || parameters.position.text == "Color") {
            return
        }

        // Map the results to add the color icon
        resultSet.runRemainingContributors(parameters) { completionResult ->
            val color = try {
                Color.valueOf(completionResult.lookupElement.lookupString)
            } catch (e: IllegalArgumentException) {
                null
            }

            var elem = LookupElementBuilder.create(
                completionResult.lookupElement.`object`,
                completionResult.lookupElement.lookupString
            )

            if (color != null) {
                elem = elem.withIcon(ColorIcon(16, color.color))
            }

            val result = completionResult.withLookupElement(elem)

            resultSet.passResult(result)
        }
    }
}