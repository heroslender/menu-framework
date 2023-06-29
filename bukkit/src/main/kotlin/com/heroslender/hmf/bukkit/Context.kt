//package com.heroslender.hmf.bukkit
//
//import com.heroslender.hmf.bukkit.manager.BukkitMenuManager
//import com.heroslender.hmf.bukkit.map.MapCanvas
//import com.heroslender.hmf.bukkit.modifiers.ClickEventData
//import com.heroslender.hmf.core.Menu
//import com.heroslender.hmf.core.ui.Component
//import com.heroslender.hmf.core.ui.Composable
//
//class Context(
//    override val manager: BukkitMenuManager,
//    override val canvas: MapCanvas,
//    override var root: Component? = null,
//) : BukkitContext {
//    override lateinit var menu: Menu
//
//    private var callback: () -> Unit = {}
//
//    override fun update() {
//        callback()
//    }
//
//    override fun onUpdate(callback: () -> Unit) {
//        this.callback = callback
//    }
//
//    override fun handleClick(x: Int, y: Int, data: ClickEventData) {
//        root?.foldOut(false) { acc, component ->
//            if (!acc) {
//                if (component.checkIntersects(x, y)) {
//                    return@foldOut component.tryClick(x, y, data)
//                }
//            }
//
//            return@foldOut acc
//        }
//    }
//}