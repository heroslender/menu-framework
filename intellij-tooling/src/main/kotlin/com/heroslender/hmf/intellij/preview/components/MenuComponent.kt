package com.heroslender.hmf.intellij.preview.components

import com.heroslender.hmf.core.ui.ComposableNode
import com.heroslender.hmf.intellij.preview.Context
import com.heroslender.hmf.intellij.preview.JetpImageUtil
import com.intellij.util.ui.ImageUtil
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.BoxLayout
import javax.swing.JPanel

class MenuComponent(node: ComposableNode) : JPanel() {
    private val image: BufferedImage

    init {
        isVisible = true
        maximumSize = Dimension(node.width, node.height)
        layout = BoxLayout(this, BoxLayout.X_AXIS)

        val dithered = (node.renderContext.canvas as Context.ICanvas).buffer
        val argb = IntArray(dithered.size)
        for (i in dithered.indices) {
            argb[i] = JetpImageUtil.getColorFromMinecraftPalette(dithered[i])
        }
        image = ImageUtil.createImage(node.width, node.height, BufferedImage.TYPE_INT_ARGB)
        image.setRGB(0, 0, node.width, node.height, argb, 0, node.width)

        repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        g.drawImage(image, 0, 0, null)
    }
}