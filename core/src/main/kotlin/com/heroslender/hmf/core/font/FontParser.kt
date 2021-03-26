package com.heroslender.hmf.core.font

import com.heroslender.hmf.core.utils.getResource
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap
import java.awt.Color
import java.awt.Font
import java.awt.FontFormatException
import java.awt.GraphicsEnvironment
import java.awt.image.BufferedImage
import java.io.IOException

@Suppress("MemberVisibilityCanBePrivate")
object FontParser {
    const val fontChars =
        " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~\u007fÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƑáíóúñÑªº¿®¬½¼¡«»"

    fun getFontFromResources(asset: String, fontName: String, size: Int = 16): com.heroslender.hmf.core.font.Font {
        try {
            val stream = getResource(asset) ?: error("Font $asset not found in resources")
            val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, stream))
        } catch (ignored: IOException) {
        } catch (ignored: FontFormatException) {
        }

        val font = Font(fontName, Font.PLAIN, size)

        val chars = Char2ObjectArrayMap<com.heroslender.hmf.core.font.Font.CharacterSprite>()
        for (char in fontChars) {
            chars[char] = getChar(font, char)
        }

        return Font(chars)
    }

    private fun getChar(font: Font, char: Char): com.heroslender.hmf.core.font.Font.CharacterSprite {
        // Create a map-sized image
        val bufferedImage = BufferedImage(128, 128, 1)
        val graphics = bufferedImage.graphics

        // Paint white background
        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, 128, 128)
        // Paint black text
        graphics.font = font
        graphics.color = Color.BLACK

        val width = graphics.fontMetrics.stringWidth(char.toString())
        val height = graphics.fontMetrics.height
        graphics.drawString(char.toString(), 0, graphics.fontMetrics.ascent)

        val rgb = bufferedImage.getRGB(0, 0, width, height, null, 0, width)

        val rows = rgb.toList().chunked(width).toMutableList()
        val data = BooleanArray(rows.size * width)

        for ((y, row) in rows.withIndex()) {
            row.forEachIndexed { x, color ->
                if (x >= width) {
                    return@forEachIndexed
                }

                data[x + y * width] = color != -1
            }
        }

        return com.heroslender.hmf.core.font.Font.CharacterSprite(width, rows.size, data)
    }
}
