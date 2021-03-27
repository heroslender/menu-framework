package com.heroslender.hmf.bukkit.sdk.nms.version

import java.util.regex.Pattern

/**
 * https://gist.github.com/SupaHam/dad1db6406596c5f8e4b221ff473831c
 *
 * @author SupaHam ([https://github.com/SupaHam](https://github.com/SupaHam))
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class NMSVersion private constructor(val major: Int, val minor: Int, val release: Int) : Comparable<NMSVersion> {

    companion object {
        private val VERSION_PATTERN = Pattern.compile("^v(\\d+)_(\\d+)_R(\\d+)")

        fun fromString(string: String): NMSVersion {
            val matcher = VERSION_PATTERN.matcher(string)

            if (!matcher.matches()) {
                throw IllegalServerException
            }

            return NMSVersion(matcher.group(1).toInt(), matcher.group(2).toInt(), matcher.group(3).toInt())
        }
    }

    fun isHigherThan(o: NMSVersion): Boolean = compareTo(o) > 0

    fun isHigherThanOrEqualTo(o: NMSVersion): Boolean = compareTo(o) >= 0

    fun isLowerThan(o: NMSVersion): Boolean = compareTo(o) < 0

    fun isLowerThanOrEqualTo(o: NMSVersion): Boolean = compareTo(o) <= 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NMSVersion) return false

        return major == other.major && minor == other.minor && release == other.release
    }

    override fun hashCode(): Int {
        var result = major
        result = 31 * result + minor
        result = 31 * result + release
        return result
    }

    override fun toString(): String {
        return "v" + major + "_" + minor + "_R" + release
    }

    override fun compareTo(other: NMSVersion): Int = when {
        major < other.major -> -1
        major > other.major -> 1
        // equal major, compare minor
        minor < other.minor -> -1
        minor > other.minor -> 1
        // equal minor, compare release
        else -> release.compareTo(other.release)
    }
}