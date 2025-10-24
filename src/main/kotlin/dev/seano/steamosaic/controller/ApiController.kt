package dev.seano.steamosaic.controller

import dev.seano.steamosaic.api.SteamApiService
import dev.seano.steamosaic.api.model.OwnedGames
import dev.seano.steamosaic.api.model.SteamId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ApiController(private val steamApiService: SteamApiService) {
    @GetMapping("/steamId")
    fun fetchSteamId(@RequestParam vanityUrl: String): ResponseEntity<SteamId?> {
        val response = steamApiService.fetchSteamId(vanityUrl)
        return ResponseEntity.ok(response.response)
    }

    @GetMapping("/ownedGames")
    fun fetchOwnedGames(@RequestParam steamId: String): ResponseEntity<OwnedGames?> {
        val response = steamApiService.fetchOwnedGames(steamId)
        return ResponseEntity.ok(response.response)
    }
}
