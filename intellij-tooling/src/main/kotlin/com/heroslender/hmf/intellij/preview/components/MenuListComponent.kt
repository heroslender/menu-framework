package com.heroslender.hmf.intellij.preview.components

import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.geom.AffineTransform
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel

class MenuListComponent(
    parent: JComponent,
    val opts: Options,
) : JPanel() {

    init {
        preferredSize = parent.preferredSize
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        alignmentX = Component.LEFT_ALIGNMENT

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