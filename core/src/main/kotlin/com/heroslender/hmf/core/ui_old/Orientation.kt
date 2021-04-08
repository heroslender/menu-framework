package com.heroslender.hmf.core.ui_old

/**
 * Defines a component orientation.
 */
enum class Orientation(val x: Int, val y: Int) {
    /**
     * Vertically aligned component.
     */
    VERTICAL(0, 1),

    /**
     * Horizontally aligned component.
     */
    HORIZONTAL(1, 0),
}