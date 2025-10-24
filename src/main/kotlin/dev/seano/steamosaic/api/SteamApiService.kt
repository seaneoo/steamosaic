package dev.seano.steamosaic.api

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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

    fun fetchGameAssets(appId: String): StoreItem {
        val inputJson =
            """{"ids":[{"appid":"$appId"}],"context":{"country_code":"US"},"data_request":{"include_assets":true}}"""
        val encoded = URLEncoder.encode(inputJson, StandardCharsets.UTF_8)
        val urlString =
            "https://api.steampowered.com/IStoreBrowseService/GetItems/v1/?key=$steamApiKey&input_json=$encoded"
        val uri = URI.create(urlString)
        val res = restClient.get().uri(uri).retrieve().body<Response<StoreItems>>()
        return res?.response?.storeItems?.first()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Game info not found")
    }

    fun getHeaderImageUrl(appId: String, hash: String): URI? {
        val urlString =
            "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/$appId/$hash/header.jpg"
        return URI.create(urlString)
    }
}
