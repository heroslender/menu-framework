package com.heroslender.hmf.intellij.preview.components

import com.intellij.util.ui.JBUI
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.geom.AffineTransform
import javax.swing.JComponent
import javax.swing.JPanel
import kotlin.math.max

class MenuListComponent(
    parent: JComponent,
    val opts: Options,
) : JPanel() {

    init {
        preferredSize = parent.preferredSize
        layout = GridBagLayout()

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                opts.released = false
                opts.startPoint = MouseInfo.getPointerInfo().location
            }

            override fun mouseReleased(e: MouseEvent?) {
                opts.released = true
                parent.repaint()
            }
        })

        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                val curPoint = e.locationOnScreen
                opts.xDiff = curPoint.x - opts.startPoint!!.x
                opts.yDiff = curPoint.y - opts.startPoint!!.y

                opts.dragger = true
                parent.repaint()
            }
        })

        addMouseWheelListener { event ->
            if (event.wheelRotation < 0) {
                opts.zoomFactor *= 1.1
            } else {
                opts.zoomFactor /= 1.1
            }
            val zoomDiv = opts.zoomFactor / opts.prevZoomFactor
            val zoomedPixels = zoomDiv * parent.width - parent.width
            opts.xOffset -= zoomedPixels * ((event.x - x) / width.toDouble())
            opts.yOffset -= zoomedPixels * ((event.y - y) / height.toDouble())

            parent.repaint()
        }
    }

    override fun add(comp: Component): Component {
        add(comp, GridBagConstraints().apply {
            anchor = GridBagConstraints.NORTH
            weighty = 1.0
            weightx = 1.0
            insets = JBUI.insetsTop(32)
        })

        revalidate()

        if (opts.xOffset == 0.0 && opts.yOffset == 0.0) {
            var maxWidth = 0
            for (i in 0 until componentCount) {
                val c = getComponent(i)

                maxWidth = max(maxWidth, c.width)
            }

            if (maxWidth > 0) {
                val parentWidth = parent.width - 64.0
                opts.zoomFactor = parentWidth / maxWidth
                val zoomedPixels = opts.zoomFactor * parent.width - parent.width
                opts.xOffset = -(zoomedPixels / 2)
                opts.yOffset = -(opts.zoomFactor * 32 - 32)
                opts.prevZoomFactor = opts.zoomFactor
                opts.zoomer = false
                println(opts)
            }
        }

        return comp
    }

    override fun paint(g: Graphics) {
        val g2 = g as Graphics2D

        with(opts) {
            if (!zoomer && !dragger) {
                val at = AffineTransform()
                at.translate(xOffset, yOffset)
                at.scale(zoomFactor, zoomFactor)
                g2.transform(at)
                reTransform = false

                return@with
            }

            if (zoomer) {
                val at = AffineTransform()

                val zoomDiv = zoomFactor / prevZoomFactor
                xOffset = zoomDiv * xOffset + (1 - zoomDiv)
                yOffset = zoomDiv * yOffset + (1 - zoomDiv)
                at.translate(xOffset, yOffset)

                at.scale(zoomFactor, zoomFactor)
                prevZoomFactor = zoomFactor
                g2.transform(at)
                zoomer = false
            }

            if (dragger) {
                val at = AffineTransform()
                at.translate(xOffset + xDiff, yOffset + yDiff)
                at.scale(zoomFactor, zoomFactor)
                g2.transform(at)
                if (released) {
                    xOffset += xDiff.toDouble()
                    yOffset += yDiff.toDouble()
                    dragger = false
                }
            }
        }

        super.paint(g)
    }

    data class Options(
        private var _zoomFactor: Double = 1.0,
        var prevZoomFactor: Double = 1.0,
        var zoomer: Boolean = false,
        var dragger: Boolean = false,
        var released: Boolean = false,
        var reTransform: Boolean = false,
        var xOffset: Double = 0.0,
        var yOffset: Double = 0.0,
        var xDiff: Int = 0,
        var yDiff: Int = 0,
        var startPoint: Point? = null,
    ) {
        var zoomFactor: Double
            get() = _zoomFactor
            set(value) {
                _zoomFactor = value
                zoomer = true
            }
    }
}