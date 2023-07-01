package com.heroslender.hmf.core

interface MenuManager<M : Menu> {

    val imageProvider: ImageProvider

    /**
     * Register the [menu] to this manager.
     */
    fun register(menu: M)

    /**
     * Removes the [menu] from this manager.
     */
    fun unregister(menu: M)

    /**
     * Disposes this menu manager, unregisters listeners and
     * cancels running tasks.
     */
    fun dispose()
}