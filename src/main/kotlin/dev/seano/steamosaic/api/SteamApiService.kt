package dev.seano.steamosaic.api

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.springframework.web.server.ResponseStatusException

@Service
class SteamApiService {
    private val restClient = RestClient.create("https://api.steampowered.com/")
    private val steamApiKey = System.getenv("STEAM_API_KEY")

    init {
        if (steamApiKey.isNullOrBlank())
            throw IllegalArgumentException(
                "Environment variable 'STEAM_API_KEY' can not be null or blank"
            )
    }

    fun fetchSteamId(name: String): Response<SteamId> {
        val uri = "ISteamUser/ResolveVanityURL/v0001/?key=$steamApiKey&vanityurl=$name&format=json"
        val res = restClient.get().uri(uri).retrieve().body<Response<SteamId>>()
        return if (res == null || res.response.success != 1) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Steam user not found")
        } else {
            res
        }
    }

    fun fetchOwnedGames(id: String): Response<OwnedGames> {
        val uri =
            "IPlayerService/GetOwnedGames/v0001/?key=$steamApiKey&steamid=$id&format=json&include_played_free_games=1&include_appinfo=1"
        val res = restClient.get().uri(uri).retrieve().body<Response<OwnedGames>>()
        return res ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Owned games not found")
    }
}
