package com.heroslender.hmf.core.ui.modifier.type

import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.Placeable
import com.heroslender.hmf.core.ui.modifier.modifiers.ClickEvent

interface CursorClickModifier : Modifier.Element {

    fun Placeable.onClick(e: ClickEvent)
}