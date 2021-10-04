package com.heroslender.hmf.intellij.preview.components

import java.awt.*
import java.awt.geom.Arc2D
import java.awt.geom.Area
import java.awt.geom.Line2D
import java.awt.geom.RoundRectangle2D
import javax.swing.border.AbstractBorder

class RoundedBorder : AbstractBorder {
    private var radii: Int
    private var insets: Insets? = null
    private var innerS = Color(166, 166, 166)
    private var outerS = Color(116, 116, 116)
    private var innerH = Color.WHITE
    private var outerH = Color.WHITE
    private var tColor = false

    constructor(radius: Int) {
        radii = radius
    }

    constructor(radius: Int, i: Insets?) {
        radii = radius
        insets = i
    }

    constructor(radius: Int, i: Insets?, out: Color, `in`: Color) {
        radii = radius
        insets = i
        innerS = `in`
        outerS = out
        tColor = true
    }

    constructor(radius: Int, i: Insets?, outH: Color, inH: Color, outS: Color, inS: Color) {
        radii = radius
        insets = i
        innerS = inS
        outerS = outS
        innerH = inH
        outerH = outH
    }

    override fun getBorderInsets(c: Component): Insets {
        return insets ?: Insets(5, 5, 5, 5)
    }

    override fun getBorderInsets(c: Component, insets: Insets): Insets {
        return getBorderInsets(c)
    }

    override fun paintBorder(c: Component, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        super.paintBorder(c, g, x, y, width, height)
        val g2d = g as Graphics2D
        g2d.setRenderingHints(RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON))
        val clip = g2d.clip
        val rect = Area(clip)
        rect.subtract(
            Area(
                RoundRectangle2D.Double(
                    x.toDouble(),
                    y.toDouble(),
                    width.toDouble(),
                    height.toDouble(),
                    radii.toDouble(),
                    radii.toDouble()
                )
            )
        )
        g2d.clip = rect
        g2d.color = c.parent.background
        g2d.fillRect(0, 0, width, height)
        g2d.clip = clip
        g2d.draw(rect)
        if (!tColor) {
            val offset = (radii / 2).toDouble()
            //Outer
            g2d.color = outerH
            g2d.draw(Line2D.Double(0.0, offset, 0.0, height - 2 - offset)) //Left
            g2d.draw(Line2D.Double(1 + offset, 0.0, width - 2 - offset, 0.0)) //Top
            g2d.draw(
                Arc2D.Double(
                    (width - radii - 1).toDouble(),
                    0.0,
                    radii.toDouble(),
                    radii.toDouble(),
                    45.0,
                    45.0,
                    Arc2D.OPEN
                )
            ) //Top Right P1
            g2d.draw(Arc2D.Double(0.0, 0.0, radii.toDouble(), radii.toDouble(), 90.0, 90.0, Arc2D.OPEN)) //Top Left
            g2d.draw(
                Arc2D.Double(
                    0.0,
                    (height - radii - 1).toDouble(),
                    radii.toDouble(),
                    radii.toDouble(),
                    180.0,
                    45.0,
                    Arc2D.OPEN
                )
            ) //Bottom Left P1

            //Inner
            g2d.color = innerH
            g2d.draw(Line2D.Double(1.0, 1 + offset, 1.0, height - 3 - offset)) //Left
            g2d.draw(Line2D.Double(2 + offset, 1.0, width - 3 - offset, 1.0)) //Top
            g2d.draw(
                Arc2D.Double(
                    (width - radii - 2).toDouble(),
                    1.0,
                    radii.toDouble(),
                    radii.toDouble(),
                    45.0,
                    45.0,
                    Arc2D.OPEN
                )
            ) //Top Right P1
            g2d.draw(Arc2D.Double(1.0, 1.0, radii.toDouble(), radii.toDouble(), 90.0, 90.0, Arc2D.OPEN)) //Top Left
            g2d.draw(
                Arc2D.Double(
                    1.0,
                    (height - radii - 2).toDouble(),
                    radii.toDouble(),
                    radii.toDouble(),
                    180.0,
                    45.0,
                    Arc2D.OPEN
                )
            ) //Bottom Left P1

            //Outer
            g2d.color = outerS
            g2d.draw(
                Line2D.Double(
                    offset,
                    (height - 1).toDouble(),
                    width - 1 - offset,
                    (height - 1).toDouble()
                )
            ) //Bottom
            g2d.draw(Line2D.Double((width - 1).toDouble(), offset, (width - 1).toDouble(), height - 2 - offset)) //Right
            g2d.draw(
                Arc2D.Double(
                    (width - radii - 1).toDouble(),
                    0.0,
                    radii.toDouble(),
                    radii.toDouble(),
                    0.0,
                    45.0,
                    Arc2D.OPEN
                )
            ) //Top Right P2
            g2d.draw(
                Arc2D.Double(
                    (width - radii - 1).toDouble(),
                    (height - radii - 1).toDouble(),
                    radii.toDouble(),
                    radii.toDouble(),
                    270.0,
                    90.0,
                    Arc2D.OPEN
                )
            ) //Bottom Right
            g2d.draw(
                Arc2D.Double(
                    0.0,
                    (height - radii - 1).toDouble(),
                    radii.toDouble(),
                    radii.toDouble(),
                    225.0,
                    45.0,
                    Arc2D.OPEN
                )
            ) //Bottom Left P2

            //Inner
            g2d.color = innerS
            g2d.draw(
                Line2D.Double(
                    1 + offset,
                    (height - 2).toDouble(),
                    width - 2 - offset,
                    (height - 2).toDouble()
                )
            ) //Bottom
            g2d.draw(
                Line2D.Double(
                    (width - 2).toDouble(),
                    1 + offset,
                    (width - 2).toDouble(),
                    height - 3 - offset
                )
            ) //Right
            g2d.draw(
                Arc2D.Double(
                    (width - radii - 2).toDouble(),
                    1.0,
                    radii.toDouble(),
                    radii.toDouble(),
                    0.0,
                    45.0,
                    Arc2D.OPEN
                )
            ) //Top Right P2
            g2d.draw(
                Arc2D.Double(
                    (width - radii - 2).toDouble(),
                    (height - radii - 2).toDouble(),
                    radii.toDouble(),
                    radii.toDouble(),
                    270.0,
                    90.0,
                    Arc2D.OPEN
                )
            ) //Bottom Right
            g2d.draw(
                Arc2D.Double(
                    1.0,
                    (height - radii - 2).toDouble(),
                    radii.toDouble(),
                    radii.toDouble(),
                    225.0,
                    45.0,
                    Arc2D.OPEN
                )
            ) //Bottom Left P2
        } else {
            g2d.color = innerS //Inner Rectangle
            g2d.draw(
                RoundRectangle2D.Double(
                    (x + 1).toDouble(),
                    (y + 1).toDouble(),
                    (width - 2).toDouble(),
                    (height - 2).toDouble(),
                    radii.toDouble(),
                    radii.toDouble()
                )
            )
            g2d.color = outerS //Outer Rectangle
            g2d.draw(
                RoundRectangle2D.Double(
                    x.toDouble(),
                    y.toDouble(),
                    (width - 2).toDouble(),
                    (height - 2).toDouble(),
                    radii.toDouble(),
                    radii.toDouble()
                )
            )
        }
    }
}