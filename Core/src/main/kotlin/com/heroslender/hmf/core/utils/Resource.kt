package com.heroslender.hmf.core.utils

import java.io.IOException
import java.io.InputStream
import java.net.URL

fun getResource(filename: String): InputStream? {
    return try {
        val url: URL = object: Any() {}.javaClass.classLoader.getResource(filename) ?: return null
        val connection = url.openConnection()
        connection.useCaches = false
        connection.getInputStream()
    } catch (ignored: IOException) {
        null
    }
}