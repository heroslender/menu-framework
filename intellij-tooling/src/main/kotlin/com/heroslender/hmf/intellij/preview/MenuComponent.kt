package com.heroslender.hmf.intellij.preview

import com.heroslender.hmf.core.IColor
import com.heroslender.hmf.core.ui.ComposableNode
import com.heroslender.hmf.core.ui.modifier.Constraints
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.ui.modifier.modifiers.background
import com.heroslender.hmf.core.ui.modifier.modifiers.maxSize
import com.intellij.util.ui.ImageUtil
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.lang.reflect.Method
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel

class MenuComponent(private val node: ComposableNode) : JPanel() {

    companion object {
        fun drawPreview(method: Method, objInstance: Any?): JComponent {
            val context = Context(Context.ICanvas(512, 380))

            val root = ComposableNode(
                parent = null,
                modifier = Modifier.maxSize(context.canvas.width, context.canvas.height).background(object : IColor {
                    override val id: Byte = 50
                    override val color: Color = Color.BLUE
                    override val isTransparent: Boolean = false
                }),
                renderContext = context
            ) {
                try {
                    method.invoke(objInstance, this)
                } catch (e: Exception) {
                    println(e.message)
                    e.printStackTrace()
                }
            }
            context.root = root

            root.compose()
            root.measure(Constraints())

            root.outerWrapper.placeAt(0, 0)

            root.draw(context.canvas)

            return MenuComponent(root)
        }
    }

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