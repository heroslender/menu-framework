package com.heroslender.hmf.core.ui_v2.modifier.type

import com.heroslender.hmf.core.ui_v2.modifier.Modifier
import com.heroslender.hmf.core.ui_v2.modifier.Placeable
import com.heroslender.hmf.core.ui_v2.modifier.modifiers.ClickEvent

interface CursorClickModifier : Modifier.Element {

    fun Placeable.onClick(e: ClickEvent)
}