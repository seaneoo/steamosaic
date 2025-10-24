package dev.seano.steamosaic.api

import com.fasterxml.jackson.annotation.JsonProperty

data class Response<T>(val response: T)

data class SteamId(val success: Int, @field:JsonProperty("steamid") val steamId: String? = null)

data class OwnedGames(@field:JsonProperty("game_count") val gameCount: Int, val games: List<Game>)

data class Game(
    @field:JsonProperty("appid") val appId: Long,
    @field:JsonProperty("playtime_forever") val playtime: Long,
)
