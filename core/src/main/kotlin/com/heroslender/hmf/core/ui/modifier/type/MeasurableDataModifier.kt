package com.heroslender.hmf.core.ui.modifier.type

import com.heroslender.hmf.core.ui.modifier.Modifier

/**
 * Modifier used to append custom data to the component
 */
interface MeasurableDataModifier : Modifier.Element {

    fun modifyData(data: Any?): Any?
}