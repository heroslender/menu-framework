package com.heroslender.hmf.intellij.preview.components

import com.heroslender.hmf.core.compose.ComposeMenu
import com.heroslender.hmf.intellij.preview.impl.JetpImageUtil
import com.heroslender.hmf.intellij.preview.impl.PreviewCanvas
import com.intellij.ui.JBColor
import com.intellij.util.ui.ImageUtil
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JPanel

class MenuComponent(val menuName: String, menu: ComposeMenu) : JPanel() {
    private var image: BufferedImage
    private var preferredSize: Dimension
    private val node = menu.rootNode

    init {
        preferredSize = Dimension(node.canvas?.width ?: 128, node.canvas?.height ?: 128)

        val dithered = (node.canvas as PreviewCanvas).buffer
        val argb = IntArray(dithered.size)
        for (i in dithered.indices) {
            argb[i] = JetpImageUtil.getColorFromMinecraftPalette(dithered[i])
        }
        image =
            ImageUtil.createImage(node.canvas?.width ?: 128, node.canvas?.height ?: 128, BufferedImage.TYPE_INT_ARGB)
        image.setRGB(0, 0, node.canvas?.width ?: 128, node.canvas?.height ?: 128, argb, 0, node.width)

        background = JBColor.CYAN

        menu.updateHandler = {
            preferredSize = Dimension(node.canvas?.width ?: 128, node.canvas?.height ?: 128)

            val dithered = (node.canvas as PreviewCanvas).buffer
            val argb = IntArray(dithered.size)
            for (i in dithered.indices) {
                argb[i] = JetpImageUtil.getColorFromMinecraftPalette(dithered[i])
            }
            image =
                ImageUtil.createImage(
                    node.canvas?.width ?: 128,
                    node.canvas?.height ?: 128,
                    BufferedImage.TYPE_INT_ARGB
                )
            image.setRGB(0, 0, node.canvas?.width ?: 128, node.canvas?.height ?: 128, argb, 0, node.width)

            revalidate();
            repaint();
        }
    }


    override fun getPreferredSize(): Dimension {
        return preferredSize
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        g.drawImage(image, 0, 0, null)
    }
}