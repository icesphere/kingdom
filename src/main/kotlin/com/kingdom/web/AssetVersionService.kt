package com.kingdom.web

import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

@Service
class AssetVersionService(private val resourceLoader: ResourceLoader) {
    private val versions = ConcurrentHashMap<String, String>()

    fun version(path: String): String {
        val normalizedPath = path.trimStart('/')
        return versions.computeIfAbsent(normalizedPath) { calculateVersion(it) }
    }

    private fun calculateVersion(path: String): String {
        val resource = resourceLoader.getResource("classpath:/static/$path")
        if (!resource.exists()) {
            return "missing"
        }

        val digest = MessageDigest.getInstance("SHA-256")
        resource.inputStream.use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (true) {
                val bytesRead = input.read(buffer)
                if (bytesRead == -1) {
                    break
                }
                digest.update(buffer, 0, bytesRead)
            }
        }

        return digest.digest().joinToString("") { "%02x".format(it.toInt() and 0xff) }.take(12)
    }
}
