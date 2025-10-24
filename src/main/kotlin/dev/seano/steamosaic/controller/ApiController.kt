package dev.seano.steamosaic.controller

import dev.seano.steamosaic.api.OwnedGames
import dev.seano.steamosaic.api.SteamApiService
import dev.seano.steamosaic.api.SteamId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ApiController(private val steamApiService: SteamApiService) {
    @GetMapping("/id")
    fun fetchSteamId(@RequestParam name: String): ResponseEntity<SteamId?> {
        val response = steamApiService.fetchSteamId(name)
        return ResponseEntity.ok(response.response)
    }

    @GetMapping("/games")
    fun fetchOwnedGames(@RequestParam id: String): ResponseEntity<OwnedGames?> {
        val response = steamApiService.fetchOwnedGames(id)
        return ResponseEntity.ok(response.response)
    }
}
