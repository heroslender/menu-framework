package com.heroslender.hmf.core.ui.modifier.modifiers

import com.heroslender.hmf.core.ui.modifier.Fill
import com.heroslender.hmf.core.ui.modifier.FitContent
import com.heroslender.hmf.core.ui.modifier.FixedSize
import com.heroslender.hmf.core.ui.modifier.Modifier

fun Modifier.fill() = this.copy(width = Fill, height = Fill)

fun Modifier.fillWidth() = this.copy(width = Fill)

fun Modifier.fillHeight() = this.copy(height = Fill)

fun Modifier.fit() = this.copy(width = FitContent, height = FitContent)

fun Modifier.fitWidth() = this.copy(width = FitContent)

fun Modifier.fitHeight() = this.copy(height = FitContent)

fun Modifier.fixedSize(width: Int, height: Int = width) = this.copy(width = FixedSize(width), height = FixedSize(height))

fun Modifier.fixedWidth(width: Int) = this.copy(width = FixedSize(width))

fun Modifier.fixedHeight(height: Int) = this.copy(height = FixedSize(height))