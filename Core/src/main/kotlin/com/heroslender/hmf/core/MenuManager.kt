package com.heroslender.hmf.core

interface MenuManager<O, M: Menu> {
    fun get(owner: O): M?

    fun remove(owner: O): M?

    fun add(menu: M)
}