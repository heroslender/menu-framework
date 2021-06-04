package com.heroslender.hmf.core.util

import com.heroslender.hmf.core.utils.getResource
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ResourceTests {

    @Test
    fun `ensures getResource returns not null value`() {
        val resource = getResource("fonts/UbuntuMono-Regular.ttf")

        assertNotNull(resource, "Got null resource but expected not null.")
    }
}