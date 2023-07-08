package com.heroslender.hmf.intellij.preview.components

import com.intellij.icons.AllIcons
import com.intellij.ide.ui.laf.darcula.DarculaUIUtil
import com.intellij.ui.Gray
import com.intellij.util.ui.JBUI
import java.awt.*
import java.awt.geom.RoundRectangle2D
import javax.swing.Icon
import javax.swing.JToolBar
import javax.swing.SwingConstants

class ToolbarComponent(listener: ToolbarListener) : JToolBar(SwingConstants.VERTICAL) {
    init {
        layout = GridLayout(0, 1, 0, 5)
        isFloatable = false
        isOpaque = false
        isBorderPainted = false
        isRollover = true
        margin = JBUI.emptyInsets()
        setLocation(50, 150)
        border = RoundedBorder(12, null, Gray.TRANSPARENT, Gray.TRANSPARENT)

        addButton(AllIcons.General.Add) {
            listener.onZoomIn()
        }
        addButton(AllIcons.General.Remove) {
            listener.onZoomOut()
        }
        addButton(AllIcons.General.ActualZoom) {
            listener.onResetZoom()
        }
        addButton(AllIcons.Actions.Restart) {
            listener.onRebuild()
        }
    }

    fun addButton(icon: Icon, onClick: () -> Unit) {
        val button = ToolbarButton(icon, onClick)

        add(button)
    }

    override fun paintComponent(g: Graphics) {
        paintBackground(g, Rectangle(width, height), JBUI.CurrentTheme.Editor.BORDER_COLOR)
    }

    fun paintBackground(g: Graphics, rect: Rectangle, color: Color) {
        val g2 = g.create() as Graphics2D
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE)
            g2.color = color
            val arc: Float = DarculaUIUtil.BUTTON_ARC.float
            g2.fill(
                RoundRectangle2D.Float(
                    rect.x.toFloat(),
                    rect.y.toFloat(),
                    rect.width.toFloat(),
                    rect.height.toFloat(),
                    arc,
                    arc
                )
            )
        } finally {
            g2.dispose()
        }
    }
}

interface ToolbarListener {
    fun onZoomIn()

    fun onZoomOut()

    fun onResetZoom()

    fun onRebuild()
}