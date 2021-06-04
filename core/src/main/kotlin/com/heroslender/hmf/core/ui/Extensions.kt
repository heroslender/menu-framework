package com.heroslender.hmf.core.ui

import com.heroslender.hmf.core.MutableState
import com.heroslender.hmf.core.MutableStateImpl
import com.heroslender.hmf.core.State

infix fun <T> Component.withState(state: State<T>): T {
    if (state is MutableStateImpl) {
        state.bind(this)
    }

    return state.value
}

fun <T> MutableState<T>.bind(component: Component) = bind(component) { component.flagDirty() }