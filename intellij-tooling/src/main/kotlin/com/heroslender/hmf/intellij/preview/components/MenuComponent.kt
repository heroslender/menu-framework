package com.heroslender.hmf.intellij.preview.components

import com.heroslender.hmf.core.compose.ComposeMenu
import com.heroslender.hmf.core.ui.LayoutNode
import com.heroslender.hmf.intellij.preview.impl.JetpImageUtil
import com.heroslender.hmf.intellij.preview.impl.PreviewCanvas
import com.heroslender.hmf.intellij.preview.impl.PreviewMenu
import com.intellij.util.ui.ImageUtil
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JPanel

class MenuComponent(private val menu: PreviewMenu) : JPanel() {
    private var image: BufferedImage? = null
    private var preferredSize: Dimension = Dimension(0, 0)
    private lateinit var node: LayoutNode

    fun update() {
        val composeMenu = ComposeMenu()
        composeMenu.updateHandler = this::onMenuUpdate
        composeMenu.start {
            menu.getUi()
            composeMenu.rootNode.canvas = menu.canvas
        }

        node = composeMenu.rootNode
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
        image = ImageUtil.createImage(canvas.width, canvas.height, BufferedImage.TYPE_INT_ARGB).apply {
            setRGB(0, 0, canvas.width, canvas.height, argb, 0, node.width)
        }

        revalidate();
        repaint();
    }


    override fun getPreferredSize(): Dimension {
        return preferredSize
    }

    override fun paintChildren(g: Graphics?) {
    }

    override fun paintComponent(g: Graphics) {
        if (image == null) {
            return
        }

        g.drawImage(image, 0, 0, null)
    }
}