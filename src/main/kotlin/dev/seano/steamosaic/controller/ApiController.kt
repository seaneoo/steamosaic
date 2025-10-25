package dev.seano.steamosaic.controller

import dev.seano.steamosaic.service.SteamService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ApiController(private val steamService: SteamService) {
    @GetMapping("/steam-id/{name}", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun fetchSteamId(@PathVariable name: String): ResponseEntity<String?> {
        val steamId = steamService.fetchSteamId(name).response.steamId
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(steamId)
    }
}
