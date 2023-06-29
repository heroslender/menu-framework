package com.heroslender.hmf.core.compose

import kotlinx.coroutines.CoroutineScope

object MenuScopeManager {
    val scopes: MutableList<CoroutineScope> = mutableListOf()
}