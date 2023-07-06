package com.heroslender.hmf.intellij.preview.components

import com.heroslender.hmf.core.compose.ComposeMenu
import com.heroslender.hmf.intellij.preview.impl.JetpImageUtil
import com.heroslender.hmf.intellij.preview.impl.PreviewCanvas
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
        menu.updateHandler = this::onMenuUpdate

        val canvas = node.canvas
        if (canvas == null) {
            image = ImageUtil.createImage(0, 0, BufferedImage.TYPE_INT_ARGB)
            preferredSize = Dimension(0, 0)
        } else {
            minimumSize = Dimension(canvas.width, canvas.height)
            preferredSize = Dimension(canvas.width, canvas.height)

            val dithered = (canvas as PreviewCanvas).buffer
            val argb = IntArray(dithered.size)
            for (i in dithered.indices) {
                argb[i] = JetpImageUtil.getColorFromMinecraftPalette(dithered[i])
            }
            image =
                ImageUtil.createImage(canvas.width, canvas.height, BufferedImage.TYPE_INT_ARGB)
            image.setRGB(0, 0, canvas.width, canvas.height, argb, 0, node.width)
        }
    }

    private fun onMenuUpdate() {
        val canvas = node.canvas ?: return

        minimumSize = Dimension(canvas.width, canvas.height)
        preferredSize = Dimension(canvas.width, canvas.height)
        maximumSize = Dimension(canvas.width, canvas.height)

        val dithered = (canvas as PreviewCanvas).buffer
        val argb = IntArray(dithered.size)
        for (i in dithered.indices) {
            argb[i] = JetpImageUtil.getColorFromMinecraftPalette(dithered[i])
        }
        image = ImageUtil.createImage(canvas.width, canvas.height, BufferedImage.TYPE_INT_ARGB)
        image.setRGB(0, 0, canvas.width, canvas.height, argb, 0, node.width)

        revalidate();
        repaint();
    }


    override fun getPreferredSize(): Dimension {
        return preferredSize
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        g.drawImage(image, 0, 0, null)
    }
}