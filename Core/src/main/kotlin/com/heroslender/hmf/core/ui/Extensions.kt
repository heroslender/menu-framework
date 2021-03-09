package com.heroslender.hmf.core.ui

import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.MutableStateImpl
import com.heroslender.hmf.core.State

typealias DrawFunc = (x: Int, y: Int, color: IColor) -> Unit

infix fun <T> Composable.withState(state: State<T>): T {
    if (state is MutableStateImpl) {
        state.bind(this)
    }

    return state.value
}