package com.kingdom.web

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.core.io.DefaultResourceLoader

class AssetVersionServiceTests {

    @Test
    fun `versions static assets from content hash`() {
        val service = AssetVersionService(DefaultResourceLoader())

        val version = service.version("js/game.js")

        assertEquals(12, version.length)
        assertTrue(version.matches(Regex("[0-9a-f]+")))
        assertEquals(version, service.version("/js/game.js"))
    }
}
