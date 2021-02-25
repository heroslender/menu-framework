package com.heroslender.hmf.bukkit.nms.version

import kotlin.Throws
import java.lang.Exception
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class NMSVersionTest {
    @Test
    @Throws(Exception::class)
    fun testMajor() {
        val v2_9_R1 = NMSVersion.fromString("v2_9_R1")

        assertEquals(2, v2_9_R1.major)
        assertEquals(9, v2_9_R1.minor)
        assertEquals(1, v2_9_R1.release)

        assertEquals(v2_9_R1.toString(), "v2_9_R1")

        assertTrue(v2_9_R1.isHigherThan(NMSVersion.fromString("v1_10_R1")))
        assertTrue(v2_9_R1.isHigherThanOrEqualTo(NMSVersion.fromString("v1_9_R1")))
    }

    @Test
    @Throws(Exception::class)
    fun testMinor() {
        val v1_10_R1 = NMSVersion.fromString("v1_10_R1")

        assertEquals(1, v1_10_R1.major)
        assertEquals(10, v1_10_R1.minor)
        assertEquals(1, v1_10_R1.release)

        assertEquals(v1_10_R1.toString(), "v1_10_R1")

        assertTrue(NMSVersion.fromString("v1_9_R1").isLowerThan(v1_10_R1))
        assertTrue(NMSVersion.fromString("v1_9_R1").isLowerThanOrEqualTo(v1_10_R1))
    }

    @Test
    @Throws(Exception::class)
    fun testRelease() {
        val v1_9_R2 = NMSVersion.fromString("v1_9_R2")

        assertEquals(1, v1_9_R2.major)
        assertEquals(9, v1_9_R2.minor)
        assertEquals(2, v1_9_R2.release)

        assertEquals(v1_9_R2.toString(), "v1_9_R2")

        assertEquals(v1_9_R2, NMSVersion.fromString("v1_9_R2"))
        assertTrue(v1_9_R2.isHigherThan(NMSVersion.fromString("v1_9_R1")))
    }
}