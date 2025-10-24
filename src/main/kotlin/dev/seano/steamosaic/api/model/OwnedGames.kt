package dev.seano.steamosaic.api.model

import com.fasterxml.jackson.annotation.JsonProperty

data class OwnedGames(@field:JsonProperty("game_count") val gameCount: Int, val games: List<Game>)

data class Game(
    @field:JsonProperty("appid") val appId: Long,
    @field:JsonProperty("playtime_forever") val playtime: Long,
)
