package com.heroslender.hmf.intellij.preview.components

import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JToolBar
import javax.swing.SwingConstants

class ToolbarComponent : JToolBar(SwingConstants.VERTICAL) {
    init {
        layout = GridLayout(0, 1, 0, 0)
        background = Color(35, 35, 35)
        isFloatable = false
        isBorderPainted = true
        isRollover = true
        margin = Insets(0, 0, 0, 0)
        setLocation(50, 150)
        border = RoundedBorder(12, null, Color(0, 0, 0, 0), Color(0, 0, 0, 0))
    }

    fun addButton(icon: Icon, onClick: (ActionEvent) -> Unit) {
        val button = JButton(icon).apply {
            background = Color(35, 35, 35)
            isOpaque = true
            margin = Insets(0, 0, 0, 0)
            verticalTextPosition = CENTER
            horizontalTextPosition = CENTER
            preferredSize = Dimension(32, 32)
            isBorderPainted = false
            isContentAreaFilled = false
            addMouseListener(object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent?) {
                    isContentAreaFilled = true
                }

                override fun mouseExited(e: MouseEvent?) {
                    isContentAreaFilled = false
                }
            })

            addActionListener { e -> onClick(e) }
        }

        add(button)
    }
}