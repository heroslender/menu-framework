package com.heroslender.hmf.bukkit.image

import org.bukkit.map.MapPalette
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.RecursiveTask
import java.util.concurrent.ThreadLocalRandom

/**
 * What a piece of optimization;
 * Performs incredibly fast Minecraft color conversion and dithering.
 *
 * @author jetp250
 */
object JetpImageUtil {
    var largestColorVal = 0
        private set

    // Test dithering of random colors
    @JvmStatic
    fun main(args: Array<String>) {
        val width = 484
        val rgb = IntArray(width * 336)
        val random: Random = ThreadLocalRandom.current()
        for (i in rgb.indices) {
            rgb[i] = random.nextInt() and 0xFFFFFF
        }
        for (i in 0..99) {
            for (j in rgb.indices) {
                rgb[j] = random.nextInt() and 0xFFFFFF
            }
            val start = System.nanoTime()
            dither(rgb, width)
            val end = System.nanoTime()
            val passed = (end - start) / 1000000.0f
            System.out.printf("Took %fms%n", passed)
        }
    }

    private val PALETTE: IntArray
    private val COLOR_MAP = ByteArray(128 * 128 * 128)
    private val FULL_COLOR_MAP = IntArray(128 * 128 * 128)
    private val COLOR_MULTIPLIERS = floatArrayOf(0.4375f, 0.1875f, 0.3125f, 0.0625f)
    fun init() {}
    fun getColorFromMinecraftPalette(`val`: Byte): Int {
        return PALETTE[(`val` + 256) % 256]
    }

    fun getBestColorIncludingTransparent(rgb: Int): Byte {
        return if (rgb ushr 24 and 0xFF == 0) 0 else getBestColor(rgb)
    }

    fun getBestColor(rgb: Int): Byte {
        return COLOR_MAP[rgb shr 16 and 0xFF shr 1 shl 14 or (rgb shr 8 and 0xFF shr 1 shl 7) or (rgb and 0xFF shr 1)]
    }

    fun getBestColor(red: Int, green: Int, blue: Int): Byte {
        return COLOR_MAP[red shr 1 shl 14 or (green shr 1 shl 7) or (blue shr 1)]
    }

    fun getBestFullColor(red: Int, green: Int, blue: Int): Int {
        return FULL_COLOR_MAP[red shr 1 shl 14 or (green shr 1 shl 7) or (blue shr 1)]
    }

    private fun computeNearest(palette: IntArray, red: Int, green: Int, blue: Int): Byte {
        var `val` = 0
        var best_distance = Float.MAX_VALUE
        for (i in 4 until palette.size) {
            val col = palette[i]
            val distance = getDistance(red, green, blue, col shr 16 and 0xFF, col shr 8 and 0xFF, col and 0xFF)
            if (distance < best_distance) {
                best_distance = distance
                `val` = i
            }
        }
        return `val`.toByte()
    }

    private fun getDistance(red: Int, green: Int, blue: Int, red2: Int, green2: Int, blue2: Int): Float {
        val red_avg = (red + red2) * .5f
        val r = red - red2
        val g = green - green2
        val b = blue - blue2
        val weight_red = 2.0f + red_avg * (1f / 256f)
        val weight_green = 4.0f
        val weight_blue = 2.0f + (255.0f - red_avg) * (1f / 256f)
        return weight_red * r * r + weight_green * g * g + weight_blue * b * b
    }

    fun simplify(buffer: IntArray): ByteArray {
        val map = ByteArray(buffer.size)
        for (index in buffer.indices) {
            val rgb = buffer[index]
            val red = rgb shr 16 and 0xFF
            val green = rgb shr 8 and 0xFF
            val blue = rgb and 0xFF
            val ptr = getBestColor(red, green, blue)
            map[index] = ptr
        }
        return map
    }

    fun ditherImage(image: Image): BufferedImage {
        val bImage = toBufferedImage(image)
        val dithered = dither(image)
        val argb = IntArray(dithered.size)
        for (i in dithered.indices) {
            argb[i] = getColorFromMinecraftPalette(dithered[i])
        }
        val newImage = BufferedImage(bImage.width, bImage.height, BufferedImage.TYPE_INT_ARGB)
        newImage.setRGB(0, 0, bImage.width, bImage.height, argb, 0, bImage.width)
        return newImage
    }

    fun dither(image: Image): ByteArray {
        val bImage = toBufferedImage(image)
        return dither2Minecraft(bImage.getRGB(0, 0, bImage.width, bImage.height, null, 0, bImage.width),
            bImage.width).array()
    }

    /**
     * Floyd-steinberg dithering with serpentine scanning
     */
    fun dither(buffer: IntArray, width: Int) {
        val height = buffer.size / width
        val widthMinus = width - 1
        val heightMinus = height - 1
        val dither_buffer = Array(2) { IntArray(width + width shl 1) }
        for (y in 0 until height) {
            val hasNextY = y < heightMinus
            val yIndex = y * width
            if (y % 2 == 0) {
                // Go left to right
                var bufferIndex = 0
                val buf1 = dither_buffer[0]
                val buf2 = dither_buffer[1]
                for (x in 0 until width) {
                    val hasPrevX = x > 0
                    val hasNextX = x < widthMinus
                    val index = yIndex + x
                    val rgb = buffer[index]
                    var red = rgb shr 16 and 0xFF
                    var green = rgb shr 8 and 0xFF
                    var blue = rgb and 0xFF

                    // Get the previous error and add
                    red = if (buf1[bufferIndex++].let { red += it; red } > 255) 255 else if (red < 0) 0 else red
                    green =
                        if (buf1[bufferIndex++].let { green += it; green } > 255) 255 else if (green < 0) 0 else green
                    blue = if (buf1[bufferIndex++].let { blue += it; blue } > 255) 255 else if (blue < 0) 0 else blue

                    // Get the closest color to the modified pixel
                    val closest = getBestFullColor(red, green, blue)

                    // Find the error
                    val delta_r = red - (closest shr 16 and 0xFF)
                    val delta_g = green - (closest shr 8 and 0xFF)
                    val delta_b = blue - (closest and 0xFF)

                    // Add to the next pixel
                    if (hasNextX) {
                        buf1[bufferIndex] = (0.4375 * delta_r).toInt()
                        buf1[bufferIndex + 1] = (0.4375 * delta_g).toInt()
                        buf1[bufferIndex + 2] = (0.4375 * delta_b).toInt()
                    }
                    if (hasNextY) {
                        if (hasPrevX) {
                            buf2[bufferIndex - 6] = (0.1875 * delta_r).toInt()
                            buf2[bufferIndex - 5] = (0.1875 * delta_g).toInt()
                            buf2[bufferIndex - 4] = (0.1875 * delta_b).toInt()
                        }
                        buf2[bufferIndex - 3] = (0.3125 * delta_r).toInt()
                        buf2[bufferIndex - 2] = (0.3125 * delta_g).toInt()
                        buf2[bufferIndex - 1] = (0.3125 * delta_b).toInt()
                        if (hasNextX) {
                            buf2[bufferIndex] = (0.0625 * delta_r).toInt()
                            buf2[bufferIndex + 1] = (0.0625 * delta_g).toInt()
                            buf2[bufferIndex + 2] = (0.0625 * delta_b).toInt()
                        }
                    }
                    buffer[index] = closest
                }
            } else {
                // Go right to left
                var bufferIndex = width + (width shl 1) - 1
                val buf1 = dither_buffer[1]
                val buf2 = dither_buffer[0]
                for (x in width - 1 downTo 0) {
                    val hasPrevX = x < widthMinus
                    val hasNextX = x > 0
                    val index = yIndex + x
                    val rgb = buffer[index]
                    var red = rgb shr 16 and 0xFF
                    var green = rgb shr 8 and 0xFF
                    var blue = rgb and 0xFF

                    // Get the previous error and add
                    blue = if (buf1[bufferIndex--].let { blue += it; blue } > 255) 255 else if (blue < 0) 0 else blue
                    green =
                        if (buf1[bufferIndex--].let { green += it; green } > 255) 255 else if (green < 0) 0 else green
                    red = if (buf1[bufferIndex--].let { red += it; red } > 255) 255 else if (red < 0) 0 else red

                    // Get the closest color to the modified pixel
                    val closest = getBestFullColor(red, green, blue)

                    // Find the error
                    val delta_r = red - (closest shr 16 and 0xFF)
                    val delta_g = green - (closest shr 8 and 0xFF)
                    val delta_b = blue - (closest and 0xFF)

                    // Add to the next pixel
                    if (hasNextX) {
                        buf1[bufferIndex] = (0.4375 * delta_b).toInt()
                        buf1[bufferIndex - 1] = (0.4375 * delta_g).toInt()
                        buf1[bufferIndex - 2] = (0.4375 * delta_r).toInt()
                    }
                    if (hasNextY) {
                        if (hasPrevX) {
                            buf2[bufferIndex + 6] = (0.1875 * delta_b).toInt()
                            buf2[bufferIndex + 5] = (0.1875 * delta_g).toInt()
                            buf2[bufferIndex + 4] = (0.1875 * delta_r).toInt()
                        }
                        buf2[bufferIndex + 3] = (0.3125 * delta_b).toInt()
                        buf2[bufferIndex + 2] = (0.3125 * delta_g).toInt()
                        buf2[bufferIndex + 1] = (0.3125 * delta_r).toInt()
                        if (hasNextX) {
                            buf2[bufferIndex] = (0.0625 * delta_b).toInt()
                            buf2[bufferIndex - 1] = (0.0625 * delta_g).toInt()
                            buf2[bufferIndex - 2] = (0.0625 * delta_r).toInt()
                        }
                    }
                    buffer[index] = closest
                }
            }
        }
    }

    fun dither2Minecraft(buffer: IntArray, width: Int): ByteBuffer {
        val height = buffer.size / width
        val widthMinus = width - 1
        val heightMinus = height - 1
        val dither_buffer = Array(2) { IntArray(width + width shl 1) }
        val data = ByteBuffer.allocate(buffer.size)
        for (y in 0 until height) {
            val hasNextY = y < heightMinus
            val yIndex = y * width
            if (y % 2 == 0) {
                // Go left to right
                var bufferIndex = 0
                val buf1 = dither_buffer[0]
                val buf2 = dither_buffer[1]
                for (x in 0 until width) {
                    val hasPrevX = x > 0
                    val hasNextX = x < widthMinus
                    val index = yIndex + x
                    val rgb = buffer[index]
                    val alpha = rgb shr 24 and 0xFF
                    var red = rgb shr 16 and 0xFF
                    var green = rgb shr 8 and 0xFF
                    var blue = rgb and 0xFF

                    // Get the previous error and add
                    red = if (buf1[bufferIndex++].let { red += it; red } > 255) 255 else if (red < 0) 0 else red
                    green =
                        if (buf1[bufferIndex++].let { green += it; green } > 255) 255 else if (green < 0) 0 else green
                    blue = if (buf1[bufferIndex++].let { blue += it; blue } > 255) 255 else if (blue < 0) 0 else blue

                    if (alpha == 0) {
                        data.put(index, 0)
                        continue
                    }

                    // Get the closest color to the modified pixel
                    val closest = getBestFullColor(red, green, blue)

                    // Find the error
                    val delta_r = red - (closest shr 16 and 0xFF)
                    val delta_g = green - (closest shr 8 and 0xFF)
                    val delta_b = blue - (closest and 0xFF)

                    // Add to the next pixel
                    if (hasNextX) {
                        buf1[bufferIndex] = (0.4375 * delta_r).toInt()
                        buf1[bufferIndex + 1] = (0.4375 * delta_g).toInt()
                        buf1[bufferIndex + 2] = (0.4375 * delta_b).toInt()
                    }
                    if (hasNextY) {
                        if (hasPrevX) {
                            buf2[bufferIndex - 6] = (0.1875 * delta_r).toInt()
                            buf2[bufferIndex - 5] = (0.1875 * delta_g).toInt()
                            buf2[bufferIndex - 4] = (0.1875 * delta_b).toInt()
                        }
                        buf2[bufferIndex - 3] = (0.3125 * delta_r).toInt()
                        buf2[bufferIndex - 2] = (0.3125 * delta_g).toInt()
                        buf2[bufferIndex - 1] = (0.3125 * delta_b).toInt()
                        if (hasNextX) {
                            buf2[bufferIndex] = (0.0625 * delta_r).toInt()
                            buf2[bufferIndex + 1] = (0.0625 * delta_g).toInt()
                            buf2[bufferIndex + 2] = (0.0625 * delta_b).toInt()
                        }
                    }
                    data.put(index, getBestColor(closest))
                }
            } else {
                // Go right to left
                var bufferIndex = width + (width shl 1) - 1
                val buf1 = dither_buffer[1]
                val buf2 = dither_buffer[0]
                for (x in width - 1 downTo 0) {
                    val hasPrevX = x < widthMinus
                    val hasNextX = x > 0
                    val index = yIndex + x
                    val rgb = buffer[index]
                    val alpha = rgb shr 24 and 0xFF
                    var red = rgb shr 16 and 0xFF
                    var green = rgb shr 8 and 0xFF
                    var blue = rgb and 0xFF

                    // Get the previous error and add
                    blue = if (buf1[bufferIndex--].let { blue += it; blue } > 255) 255 else if (blue < 0) 0 else blue
                    green =
                        if (buf1[bufferIndex--].let { green += it; green } > 255) 255 else if (green < 0) 0 else green
                    red = if (buf1[bufferIndex--].let { red += it; red } > 255) 255 else if (red < 0) 0 else red

                    if (alpha == 0) {
                        data.put(index, 0)
                        continue
                    }
                    // Get the closest color to the modified pixel
                    val closest = getBestFullColor(red, green, blue)

                    // Find the error
                    val delta_r = red - (closest shr 16 and 0xFF)
                    val delta_g = green - (closest shr 8 and 0xFF)
                    val delta_b = blue - (closest and 0xFF)

                    // Add to the next pixel
                    if (hasNextX) {
                        buf1[bufferIndex] = (0.4375 * delta_b).toInt()
                        buf1[bufferIndex - 1] = (0.4375 * delta_g).toInt()
                        buf1[bufferIndex - 2] = (0.4375 * delta_r).toInt()
                    }
                    if (hasNextY) {
                        if (hasPrevX) {
                            buf2[bufferIndex + 6] = (0.1875 * delta_b).toInt()
                            buf2[bufferIndex + 5] = (0.1875 * delta_g).toInt()
                            buf2[bufferIndex + 4] = (0.1875 * delta_r).toInt()
                        }
                        buf2[bufferIndex + 3] = (0.3125 * delta_b).toInt()
                        buf2[bufferIndex + 2] = (0.3125 * delta_g).toInt()
                        buf2[bufferIndex + 1] = (0.3125 * delta_r).toInt()
                        if (hasNextX) {
                            buf2[bufferIndex] = (0.0625 * delta_b).toInt()
                            buf2[bufferIndex - 1] = (0.0625 * delta_g).toInt()
                            buf2[bufferIndex - 2] = (0.0625 * delta_r).toInt()
                        }
                    }
                    data.put(index, getBestColor(closest))
                }
            }
        }
        return data
    }

    //    /**
    //     * Dither an rgb buffer
    //     *
    //     * @param width
    //     * The width of the image
    //     * @param buffer
    //     * RGB buffer
    //     * @return
    //     * Dithered image in minecraft colors
    //     */
    //	public static byte[] dither( int width, int[] buffer ) {
    //		int height = buffer.length / width;
    //
    //		float[] mult = COLOR_MULTIPLIERS;
    //
    //		int[][] dither_buffer = new int[ 2 ][ Math.max( width, height ) * 3 ];
    //
    //		byte[] map = new byte[ buffer.length ];
    //		int[] y_temps = { 0, 1, 1, 1 };
    //		int[] x_temps = { 1, -1, 0, 1 };
    //		for (int x = 0; x < width; ++x) {
    //			dither_buffer[ 0 ] = dither_buffer[ 1 ];
    //			dither_buffer[ 1 ] = new int[ Math.max( width, height ) * 3 ];
    //			int[] buffer2 = dither_buffer[ 0 ];
    //			for ( int y = 0; y < height; ++y ) {
    //				int rgb = buffer[ y * width + x ];
    //
    //				int red   = rgb >> 16 & 0xFF;
    //				int green = rgb >> 8  & 0xFF;
    //				int blue  = rgb       & 0xFF;
    //
    //				int index = y + ( y << 1 );
    //
    //				red   = ( red   += buffer2[ index++ ] ) > 255 ? 255 : red   < 0 ? 0 : red;
    //				green = ( green += buffer2[ index++ ] ) > 255 ? 255 : green < 0 ? 0 : green;
    //				blue  = ( blue  += buffer2[ index   ] ) > 255 ? 255 : blue  < 0 ? 0 : blue;
    //				int matched_color = PALETTE[ Byte.toUnsignedInt( getBestColor( red, green, blue ) ) ];
    //				int delta_r = red   - ( matched_color >> 16 & 0xFF );
    //				int delta_g = green - ( matched_color >> 8  & 0xFF );
    //				int delta_b = blue  - ( matched_color       & 0xFF );
    //				for ( int i = 0; i < x_temps.length; i++ ) {
    //					int temp_y = y_temps[ i ];
    //					int temp_x;
    //					if ( temp_y < height && ( temp_x = y + x_temps[i] ) < width && temp_x > 0 ) {
    //						int[] buffer3 = dither_buffer[ temp_y ];
    //						float scalar = mult[ i ];
    //						index = temp_x + ( temp_x << 1 );
    //						buffer3[ index ] = ( int ) ( buffer3[index++] + scalar * delta_r );
    //						buffer3[ index ] = ( int ) ( buffer3[index++] + scalar * delta_g );
    //						buffer3[ index ] = ( int ) ( buffer3[index  ] + scalar * delta_b );
    //					}
    //				}
    //				if ( ( rgb >> 24 & 0xFF ) < 0x80 ) {
    //					map[ y * width + x ] = 0;
    //				} else {
    //					map[ y * width + x ] = COLOR_MAP[ red >> 1 << 14 | green >> 1 << 7 | blue >> 1 ];
    //				}
    //			}
    //		}
    //		return map;
    //	}
    fun getSubImage(
        topCornerX: Int,
        topCornerY: Int,
        width: Int,
        height: Int,
        image: IntArray,
        imageWidth: Int,
    ): IntArray {
        val subimage = IntArray(width * height)
        val imageHeight = image.size / imageWidth
        val topX = Math.max(0, topCornerX)
        val topY = Math.max(0, topCornerY)
        val imgWidth = Math.min(imageWidth - topCornerX, width)
        val imgHeight = Math.min(imageHeight - topCornerY, height)
        for (x in 0 until imgWidth) {
            for (y in 0 until imgHeight) {
                subimage[x + y * width] = image[x + topX + (y + topY) * imageWidth]
            }
        }
        return subimage
    }

    fun overlay(x: Int, y: Int, image: IntArray, imageWidth: Int, canvas: IntArray, canvasWidth: Int) {
        val height = canvas.size / canvasWidth
        val imageHeight = image.size / imageWidth
        val widthData = getSubsegment(0, canvasWidth, x, imageWidth)
        val heightData = getSubsegment(0, height, y, imageHeight)
        val widthStart = widthData[0]
        val widthEnd = widthData[1]
        val widthLength = widthEnd - widthStart
        val heightStart = heightData[0]
        val heightEnd = heightData[1]
        val heightLength = heightEnd - heightStart
        for (offY in 0 until heightLength) {
            val canvasIndexY = (heightStart + offY) * canvasWidth + widthStart
            val imageIndexY = (heightStart - y + offY) * imageWidth
            for (offX in 0 until widthLength) {
                canvas[offX + canvasIndexY] = image[widthStart - x + offX + imageIndexY]
            }
        }
    }

    fun toBufferedImage(img: Image): BufferedImage {
        if (img is BufferedImage) {
            return img
        }
        val bimage = BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB)
        val bGr = bimage.createGraphics()
        bGr.drawImage(img, 0, 0, null)
        bGr.dispose()
        return bimage
    }

    fun getRGBArray(image: BufferedImage): IntArray {
        return image.getRGB(0, 0, image.width, image.height, null, 0, image.width)
    }

    fun getSubsegment(start: Int, length: Int, substart: Int, sublength: Int): IntArray {
        val relativeStart = Math.min(start + length, Math.max(start, substart))
        val relativeEnd = Math.max(start, Math.min(start + length, substart + sublength))
        return intArrayOf(relativeStart, relativeEnd)
    }

    fun resize(data: ByteArray, originalWidth: Int, width: Int, height: Int): ByteArray {
        val size = width * height
        val scaled = ByteArray(size)
        val originalHeight = data.size / originalWidth
        val widthRatio = originalWidth / width.toDouble()
        val heightRatio = originalHeight / height.toDouble()
        for (y in 0 until height) {
            val scaledY = (y * heightRatio).toInt() * originalWidth
            val yHeight = y * width
            for (x in 0 until width) {
                val scaledX = (x * widthRatio).toInt()
                scaled[x + yHeight] = data[scaledX + scaledY]
            }
        }
        return scaled
    }

    fun rotate(original: ByteArray, width: Int, copy: ByteArray, copyWidth: Int, radians: Double): ByteArray {
        val height = original.size / width
        val copyHeight = copy.size / copyWidth
        val cos = Math.cos(radians)
        val sin = Math.sin(radians)
        val xo = (width - 1) * .5
        val yo = (height - 1) * .5
        val copyXHalf = (copyWidth - 1) * .5
        val copyYHalf = (copyHeight - 1) * .5
        for (y in 0 until copyHeight) {
            val b = y - copyYHalf
            val yHeight = y * copyWidth
            for (x in 0 until copyWidth) {
                val a = x - copyXHalf
                val xx = (a * cos - b * sin + xo + .5).toInt()
                val yy = (a * sin + b * cos + yo + .5).toInt()
                if (xx >= 0 && xx < width && yy >= 0 && yy < height) {
                    copy[x + yHeight] = original[xx + yy * width]
                }
            }
        }
        return copy
    }

    /**
     * Takes in 2 colors and sets one as the foreground
     *
     * @param baseColor
     * The background color
     * @param overlay
     * The foreground
     * @return
     * The colors combined, if the foreground is not opaque
     */
    fun overwriteColor(baseColor: Int, overlay: Int): Int {
        val a2 = overlay ushr 24 and 0xFF
        if (a2 == 0) {
            return baseColor
        } else if (a2 == 0xFF) {
            return overlay
        }
        val r2 = overlay ushr 16 and 0xFF
        val g2 = overlay ushr 8 and 0xFF
        val b2 = overlay and 0xFF
        val a1 = Math.max(baseColor ushr 24 and 0xFF, a2)
        val r1 = baseColor ushr 16 and 0xFF
        val g1 = baseColor ushr 8 and 0xFF
        val b1 = baseColor and 0xFF
        val percent = a2 / 255.0
        val unPercent = 1 - percent
        val r = (r1 * unPercent + r2 * percent).toInt()
        val g = (g1 * unPercent + g2 * percent).toInt()
        val b = (b1 * unPercent + b2 * percent).toInt()
        return a1 shl 24 or (r shl 16) or (g shl 8) or b
    }

    fun mixColors(color1: Int, color2: Int): Int {
        val a2 = color2 shr 24 and 0xFF
        val r2 = color2 shr 16 and 0xFF
        val g2 = color2 shr 8 and 0xFF
        val b2 = color2 and 0xFF
        val r1 = color1 shr 16 and 0xFF
        val g1 = color1 shr 8 and 0xFF
        val b1 = color1 and 0xFF
        val percent = a2 / 255.0
        val r = ((r1 + r2 * percent) / 2).toInt()
        val g = ((g1 + g2 * percent) / 2).toInt()
        val b = ((b1 + b2 * percent) / 2).toInt()
        return r shl 16 or (g shl 8) or b
    }

    fun mediateARGB(c1: Int, c2: Int): Int {
        val a1 = c1 and -0x1000000 ushr 24
        val r1 = c1 and 0x00FF0000 shr 16
        val g1 = c1 and 0x0000FF00 shr 8
        val b1 = c1 and 0x000000FF
        val a2 = c2 and -0x1000000 ushr 24
        val r2 = c2 and 0x00FF0000 shr 16
        val g2 = c2 and 0x0000FF00 shr 8
        val b2 = c2 and 0x000000FF
        val am = (a1 + a2) / 2
        val rm = (r1 + r2) / 2
        val gm = (g1 + g2) / 2
        val bm = (b1 + b2) / 2
        return am shl 24 or (rm shl 16) or (gm shl 8) or bm
    }

    /**
     * Brightens a given color for a percent; Negative values darken the color.
     *
     * @param c
     * The color to brighten.
     * @param percent
     * The percentage to brighten; Must not exceed 100 percent.
     * @return
     * The new brightened color.
     */
    fun brightenColor(c: Color, percent: Int): Color {
        if (percent == 0) return c
        val r = c.red
        val g = c.green
        val b = c.blue
        if (percent > 0) {
            val newr = r + percent * (255 - r) / 100
            val newg = g + percent * (255 - g) / 100
            val newb = b + percent * (255 - b) / 100
            return Color(newr, newg, newb)
        }
        val newr = r + percent * r / 100
        val newg = g + percent * g / 100
        val newb = b + percent * b / 100
        return Color(newr, newg, newb, c.alpha)
    }

    init {
        val colors: MutableList<Int> = ArrayList()
        val start = System.nanoTime()
        for (i in 0..255) {
            try {
                val color = MapPalette.getColor(i.toByte())
                colors.add(color.getRGB())
            } catch (e: IndexOutOfBoundsException) {
                println("Captured " + (i - 1) + " colors!")
                largestColorVal = i - 1
                break
            }
        }
        PALETTE = IntArray(colors.size)
        var index = 0
        for (color in colors) {
            PALETTE[index++] = color
        }
        PALETTE[0] = 0

        // ForkJoinPool'd the loading of colors
        val tasks: MutableList<LoadRed> = ArrayList(128)
        var r = 0
        while (r < 256) {
            val red = LoadRed(PALETTE, r)
            tasks.add(red)
            red.fork()
            r += 2
        }
        for (i in 0..127) {
            val sub: ByteArray = tasks.get(i).join()
            val ci = i shl 14
            for (si in 0..16383) {
                COLOR_MAP[ci + si] = sub.get(si)
                FULL_COLOR_MAP[ci + si] =
                    PALETTE[java.lang.Byte.toUnsignedInt(sub.get(si))]
            }
        }

        // Original method
//		for ( int r = 0; r < 256; r += 2 ) {
//			for ( int g = 0; g < 256; g += 2 ) {
//				for ( int b = 0; b < 256; b += 2 ) {
//					int colorIndex = r >> 1 << 14 | g >> 1 << 7 | b >> 1;
//
//					int val = 0;
//					float best_distance = Float.MAX_VALUE;
//					float distance = 0;
//					int col = 0;
//					for (int i = 4; i < PALETTE.length; ++i) {
//						col = PALETTE[i];
//						int r2 = col >> 16 & 0xFF;
//						int g2 = col >> 8 & 0xFF;
//						int b2 = col & 0xFF;
//
//						float red_avg = ( r + r2 ) * .5f;
//						int redVal = r - r2;
//						int greenVal = g - g2;
//						int blueVal = b - b2;
//						float weight_red = 2.0f + red_avg * ( 1f / 256f );
//						float weight_green = 4.0f;
//						float weight_blue = 2.0f + ( 255.0f - red_avg ) * ( 1f / 256f );
//						distance = weight_red * redVal * redVal + weight_green * greenVal * greenVal + weight_blue * blueVal * blueVal;
//
//						if (distance < best_distance) {
//							best_distance = distance;
//							val = i;
//						}
//					}
//					COLOR_MAP[ colorIndex ] = ( byte ) val;
//				}
//			}
//		}
        val end = System.nanoTime()
        println("Initial lookup table initialized in " + (end - start) / 1000000.0 + " ms")
    }
}

internal class LoadRed(protected val palette: IntArray, protected val r: Int) : RecursiveTask<ByteArray>() {
    override fun compute(): ByteArray {
        val greenSub: MutableList<LoadGreen> = ArrayList(128)
        var g = 0
        while (g < 256) {
            val green = LoadGreen(palette, r, g)
            greenSub.add(green)
            green.fork()
            g += 2
        }
        val vals = ByteArray(16384)
        for (i in 0..127) {
            val sub = greenSub[i].join()
            val index = i shl 7
            for (si in 0..127) {
                vals[index + si] = sub!![si]
            }
        }
        return vals
    }
}

internal class LoadGreen(protected val palette: IntArray, protected val r: Int, protected val g: Int) :
    RecursiveTask<ByteArray>() {
    override fun compute(): ByteArray {
        val blueSub: MutableList<LoadBlue> = ArrayList(128)
        var b = 0
        while (b < 256) {
            val blue = LoadBlue(palette, r, g, b)
            blueSub.add(blue)
            blue.fork()
            b += 2
        }
        val matches = ByteArray(128)
        for (i in 0..127) {
            matches[i] = blueSub[i].join()
        }
        return matches
    }
}

internal class LoadBlue(
    protected val palette: IntArray,
    protected val r: Int,
    protected val g: Int,
    protected val b: Int,
) : RecursiveTask<Byte>() {
    override fun compute(): Byte {
        var `val` = 0
        var best_distance = Float.MAX_VALUE
        var distance = 0f
        var col = 0
        for (i in 4 until palette.size) {
            col = palette[i]
            val r2 = col shr 16 and 0xFF
            val g2 = col shr 8 and 0xFF
            val b2 = col and 0xFF
            val red_avg = (r + r2) * .5f
            val redVal = r - r2
            val greenVal = g - g2
            val blueVal = b - b2
            val weight_red = 2.0f + red_avg * (1f / 256f)
            val weight_green = 4.0f
            val weight_blue = 2.0f + (255.0f - red_avg) * (1f / 256f)
            distance =
                weight_red * redVal * redVal + weight_green * greenVal * greenVal + weight_blue * blueVal * blueVal
            if (distance < best_distance) {
                best_distance = distance
                `val` = i
            }
        }
        return `val`.toByte()
    }
}