package dev.seano.steamosaic.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
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

@JsonIgnoreProperties(ignoreUnknown = true)
data class StoreItem(
    @field:JsonProperty("appid") val appId: Long,
    val name: String? = null,
    val assets: Assets? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Assets(@field:JsonProperty("small_capsule") val smallCapsule: String? = null)
