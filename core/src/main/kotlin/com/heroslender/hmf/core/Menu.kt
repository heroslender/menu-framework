package com.heroslender.hmf.core

import androidx.compose.runtime.Composable

interface Menu {
    @Composable
    fun getUi()

    fun close()
}