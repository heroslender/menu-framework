@file:Suppress("NOTHING_TO_INLINE")

package com.heroslender.hmf.core.ui_old.modifier


/**
 * Concatenates this modifiers extras with another.
 *
 * Returns a [Modifier] representing this modifier followed by [other] in sequence.
 */
inline infix fun Modifier.then(other: ModifierExtra): Modifier {
    if (other != ModifierExtra) {
        return copy(extra = this.extra then other)
    }

    return this
}

/**
 * Concatenates this modifiers extras with another.
 *
 * Returns a [Modifier] representing this modifier followed by [other] in sequence.
 */
inline infix fun <reified T: ModifierExtra> Modifier.thenDefault(other: T): Modifier {
    if (other != ModifierExtra && !this.extra.any { it is T }) {
        return copy(extra = this.extra then other)
    }

    return this
}

interface ModifierExtra {

    /**
     * Accumulates a value starting with [initial] and applying [operation] to the current value
     * and each element from outside in.
     */
    fun <R> foldIn(initial: R, operation: (R, Element) -> R): R

    /**
     * Accumulates a value starting with [initial] and applying [operation] to the current value
     * and each element from inside out.
     */
    fun <R> foldOut(initial: R, operation: (Element, R) -> R): R

    /**
     * Returns `true` if the [predicate] matches any [Element] in this chain.
     */
    fun any(predicate: (Element) -> Boolean): Boolean

    /**
     * Returns `true` if [predicate] matches all [Element]s in this chain or if
     * this contains no [Element]s.
     */
    fun all(predicate: (Element) -> Boolean): Boolean

    /**
     * Concatenates this extra with another.
     *
     * Returns a [ModifierExtra] representing this followed by [other] in sequence.
     */
    infix fun then(other: ModifierExtra): ModifierExtra =
        if (other === ModifierExtra) this else CombinedModifier(this, other)


    /**
     * A single element contained within a [ModifierExtra] chain.
     */
    interface Element : ModifierExtra {
        override fun <R> foldIn(initial: R, operation: (R, Element) -> R): R =
            operation(initial, this)

        override fun <R> foldOut(initial: R, operation: (Element, R) -> R): R =
            operation(this, initial)

        override fun any(predicate: (Element) -> Boolean): Boolean = predicate(this)

        override fun all(predicate: (Element) -> Boolean): Boolean = predicate(this)
    }

    companion object : ModifierExtra {
        override fun <R> foldIn(initial: R, operation: (R, Element) -> R): R = initial
        override fun <R> foldOut(initial: R, operation: (Element, R) -> R): R = initial
        override fun any(predicate: (Element) -> Boolean): Boolean = false
        override fun all(predicate: (Element) -> Boolean): Boolean = true
        override infix fun then(other: ModifierExtra): ModifierExtra = other
        override fun toString() = "Modifier"
    }
}

/**
 * A node in a [ModifierExtra] chain. A CombinedModifier always contains at least two elements;
 * a Modifier [outer] that wraps around the Modifier [inner].
 */
class CombinedModifier(
    private val outer: ModifierExtra,
    private val inner: ModifierExtra
) : ModifierExtra {
    override fun <R> foldIn(initial: R, operation: (R, ModifierExtra.Element) -> R): R =
        inner.foldIn(outer.foldIn(initial, operation), operation)

    override fun <R> foldOut(initial: R, operation: (ModifierExtra.Element, R) -> R): R =
        outer.foldOut(inner.foldOut(initial, operation), operation)

    override fun any(predicate: (ModifierExtra.Element) -> Boolean): Boolean =
        outer.any(predicate) || inner.any(predicate)

    override fun all(predicate: (ModifierExtra.Element) -> Boolean): Boolean =
        outer.all(predicate) && inner.all(predicate)

    override fun equals(other: Any?): Boolean =
        other is CombinedModifier && outer == other.outer && inner == other.inner

    override fun hashCode(): Int = outer.hashCode() + 31 * inner.hashCode()

    override fun toString() = "[" + foldIn("") { acc, element ->
        if (acc.isEmpty()) element.toString() else "$acc, $element"
    } + "]"
}