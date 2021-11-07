package com.heroslender.hmf.core.utils

import java.io.IOException
import java.io.InputStream
import java.net.URL

/**
 * Get a resource [InputStream] from the jar located at [fileName].
 */
inline fun getResource(fileName: String, classLoader: ClassLoader = object : Any() {}.javaClass.classLoader): InputStream? {
    return try {
        val url: URL = classLoader.getResource(fileName) ?: return null
        val connection = url.openConnection()
        connection.useCaches = false
        connection.getInputStream()
    } catch (ignored: IOException) {
        null
    }
}