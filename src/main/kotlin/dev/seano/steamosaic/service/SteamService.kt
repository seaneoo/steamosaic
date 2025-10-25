package dev.seano.steamosaic.service

import dev.seano.steamosaic.model.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
class SteamService() {
    private val restClient = RestClient.create("https://api.steampowered.com/")
    private val steamApiKey = System.getenv("STEAM_API_KEY")
    private val logger: Logger = LoggerFactory.getLogger(SteamService::class.java.name)

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

    fun fetchGameAssets(appIds: Array<String>): List<StoreItem> {
        if (appIds.size !in 1..10) {
            logger.warn("Invalid number of app IDs: ${appIds.size}. Must be between 1 and 10.")
            return emptyList()
        }

        val appIdsJson =
            appIds.joinToString(separator = ",", prefix = "[", postfix = "]") {
                """{"appid":"$it"}"""
            }
        val inputJson =
            """{"ids":$appIdsJson,"context":{"country_code":"US"},"data_request":{"include_assets":true}}"""

        val encoded = URLEncoder.encode(inputJson, StandardCharsets.UTF_8)
        val uriString =
            "https://api.steampowered.com/IStoreBrowseService/GetItems/v1/?key=$steamApiKey&input_json=$encoded"
        val uri = URI.create(uriString)
        logger.info("Fetching game assets from URL: $uri")

        val res = restClient.get().uri(uri).retrieve().body<Response<StoreItems>>()
        return res?.response?.storeItems
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Game info not found")
    }

    fun getHeaderImageUrl(appId: String, path: String): String {
        return "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/$appId/$path"
    }

    fun getSteamId(identifier: String): String? {
        return if (Regex("^\\d+$").matches(identifier)) identifier
        else fetchSteamId(identifier).response.steamId
    }
}
