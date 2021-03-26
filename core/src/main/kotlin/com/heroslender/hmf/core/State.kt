@file:Suppress("NOTHING_TO_INLINE")

package com.heroslender.hmf.core

import com.heroslender.hmf.core.ui.Component
import java.lang.ref.WeakReference
import java.util.*
import kotlin.reflect.KProperty

interface State<T> {
    val value: T
}

fun <T> stateOf(value: T): State<T> = StateImpl(value)

inline operator fun <T> State<T>.getValue(thisObj: Any?, property: KProperty<*>): T = value

interface MutableState<T> : State<T> {
    override var value: T
}

fun <T> mutableStateOf(value: T): MutableState<T> {
    return MutableStateImpl(value)
}

inline operator fun <T> MutableState<T>.setValue(thisObj: Any, property: KProperty<*>, newValue: T) {
    this.value = newValue
}

class StateImpl<T>(override val value: T): State<T>

class MutableStateImpl<T>(value: T) : MutableState<T> {
    private val callbacksMap: MutableMap<WeakReference<Any>, () -> Unit> = IdentityHashMap()

    override var value: T = value
        set(value) {
            if (field == value) {
                return
            }

            field = value

            synchronized(callbacksMap) {
                val it = callbacksMap.iterator()
                while (it.hasNext()) {
                    val entry = it.next()
                    if (entry.key.get() == null) {
                        it.remove()
                        continue
                    }

                    entry.value.invoke()
                }
            }
        }

    private fun onValueChange(holder: Any, callback: () -> Unit) {
        synchronized(callbacksMap) {
            val it = callbacksMap.iterator()
            while (it.hasNext()) {
                val key = it.next().key.get()
                if (key == null) {
                    it.remove()
                    continue
                } else if (key === holder) {
                    // Already present here, ignoring
                    // Should it be replaced instead?
                    continue
                }
            }

            this.callbacksMap[WeakReference(holder)] = callback
        }
    }

    fun bind(component: Component) {
        onValueChange(component) { component.flagDirty() }
    }

    fun bind(holder: Any, callback: () -> Unit) = onValueChange(holder, callback)
}