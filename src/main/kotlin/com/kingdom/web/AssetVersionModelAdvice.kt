package com.kingdom.web

import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

@ControllerAdvice
class AssetVersionModelAdvice(private val assetVersionService: AssetVersionService) {

    @ModelAttribute("assetVersions")
    fun assetVersions(): AssetVersionService {
        return assetVersionService
    }
}
