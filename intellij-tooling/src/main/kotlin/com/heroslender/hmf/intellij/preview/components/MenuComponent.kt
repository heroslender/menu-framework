package com.heroslender.hmf.intellij.preview.components

import com.heroslender.hmf.core.ui.ComposableNode
import com.heroslender.hmf.intellij.preview.impl.JetpImageUtil
import com.heroslender.hmf.intellij.preview.impl.PreviewCanvas
import com.intellij.ui.JBColor
import com.intellij.util.ui.ImageUtil
import java.awt.Dimension
import java.awt.Graphics
import java.awt.GridBagLayout
import java.awt.image.BufferedImage
import javax.swing.JPanel

class MenuComponent(val menuName: String, node: ComposableNode) : JPanel() {
    private val image: BufferedImage
    private val preferredSize: Dimension

    init {
        layout = GridBagLayout()
        preferredSize = Dimension(node.width, node.height)

        val dithered = (node.renderContext.canvas as PreviewCanvas).buffer
        val argb = IntArray(dithered.size)
        for (i in dithered.indices) {
            argb[i] = JetpImageUtil.getColorFromMinecraftPalette(dithered[i])
        }
        image = ImageUtil.createImage(node.width, node.height, BufferedImage.TYPE_INT_ARGB)
        image.setRGB(0, 0, node.width, node.height, argb, 0, node.width)

        background = JBColor.CYAN
    }

    override fun getPreferredSize(): Dimension {
        return preferredSize
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        g.drawImage(image, 0, 0, null)
    }
}