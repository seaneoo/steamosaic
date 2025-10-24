package dev.seano.steamosaic.api

import com.fasterxml.jackson.annotation.JsonProperty

data class Response<T>(val response: T)

data class SteamId(
    @field:JsonProperty(access = JsonProperty.Access.WRITE_ONLY) val success: Int,
    @field:JsonProperty("steamid") val steamId: String? = null,
)

data class OwnedGames(@field:JsonProperty("game_count") val gameCount: Int, val games: List<Game>)

data class Game(
    @field:JsonProperty("appid") val appId: Long,
    val name: String,
    @field:JsonProperty("playtime_forever") val playtime: Long,
)

data class StoreItems(@field:JsonProperty("store_items") val storeItems: List<StoreItem>)

data class StoreItem(
    @field:JsonProperty("appid") val appId: Long,
    val name: String,
    val assets: Assets,
)

data class Assets(val header: String)
