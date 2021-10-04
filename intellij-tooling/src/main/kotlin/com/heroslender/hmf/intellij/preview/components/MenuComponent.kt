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

class MenuComponent(private val node: ComposableNode) : JPanel() {

    init {
        isVisible = true
        maximumSize = Dimension(node.width, node.height)
        layout = BoxLayout(this, BoxLayout.X_AXIS)
    }

    override fun paintComponent(g: Graphics) {
        val dithered = (node.renderContext.canvas as Context.ICanvas).buffer
        val argb = IntArray(dithered.size)
        for (i in dithered.indices) {
            argb[i] = JetpImageUtil.getColorFromMinecraftPalette(dithered[i])
        }
        val newImage = ImageUtil.createImage(node.width, node.height, BufferedImage.TYPE_INT_ARGB)
        newImage.setRGB(0, 0, node.width, node.height, argb, 0, node.width)

        g.drawImage(newImage, 0, 0, null)
    }
}