package dev.seano.steamosaic.controller

import dev.seano.steamosaic.service.MosaicService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class DefaultController(private val mosaicService: MosaicService) {
    @GetMapping
    fun index(): String {
        @Suppress("SpringMVCViewInspection")
        return "index"
    }

    @GetMapping("/{steamId}.png", produces = [MediaType.IMAGE_PNG_VALUE])
    fun mosaic(@PathVariable steamId: String): ResponseEntity<ByteArray?> {
        val mosaic = mosaicService.buildMosaic(steamId)
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(mosaic)
    }
}
