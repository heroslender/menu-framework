package com.heroslender.hmf.bukkit.map

@Suppress("unused")
data class MapIcon(
    /**
     * Position of the icon in the `x` coordinates.
     *
     * Unlike the map size, this ranges from -128 to 128.
     */
    val x: Byte,
    /**
     * Position of the icon in the `y` coordinates.
     *
     * Unlike the map size, this ranges from -128 to 128.
     */
    val y: Byte,
    /**
     * [direction] must be in the range 0-15
     */
    val direction: Byte = 0,
    /**
     * The type of the icon.
     */
    val type: Type,
) {

    constructor(x: Int, z: Int, direction: Byte, type: Type) :
            this(x.toByte(), z.toByte(), direction, type)

    enum class Type(val typeId: Byte) {
        WHITE_POINTER(0),
        GREEN_POINTER(1),
        RED_POINTER(2),
        BLUE_POINTER(3),
        WHITE_CROSS(4),

        /**
         * Since 1.8.1 ?!?
         */
        RED_TRIANGLE(5),

        /**
         * Since 1.8.1 ?!?
         */
        LARGE_WHITE_DOT(6),

        /**
         * Since 1.11
         */
        WHITE_DOT(7),

        /**
         * Since 1.11
         */
        WOODLAND_MANSION(8),

        /**
         * Since 1.11
         */
        OCEAN_MONUMENT(9),

        /**
         * Since 1.13
         */
        WHITE_BANNER(10),

        /**
         * Since 1.13
         */
        ORANGE_BANNER(11),

        /**
         * Since 1.13
         */
        MAGENTA_BANNER(12),

        /**
         * Since 1.13
         */
        BLUE_BANNER_LIGHT(13),

        /**
         * Since 1.13
         */
        YELLOW_BANNER(14),

        /**
         * Since 1.13
         */
        LIME_BANNER(15),

        /**
         * Since 1.13
         */
        PINK_BANNER(16),

        /**
         * Since 1.13
         */
        GRAY_BANNER(17),

        /**
         * Since 1.13
         */
        GRAY_BANNER_LIGHT(18),

        /**
         * Since 1.13
         */
        CYAN_BANNER(19),

        /**
         * Since 1.13
         */
        PURPLE_BANNER(20),

        /**
         * Since 1.13
         */
        BLUE_BANNER(21),

        /**
         * Since 1.13
         */
        BROWN_BANNER(22),

        /**
         * Since 1.13
         */
        GREEN_BANNER(23),

        /**
         * Since 1.13
         */
        RED_BANNER(24),

        /**
         * Since 1.13
         */
        BLACK_BANNER(25),

        /**
         * Since 1.13
         */
        RED_CROSS(26),
    }
}
