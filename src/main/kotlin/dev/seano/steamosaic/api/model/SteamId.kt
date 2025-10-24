package dev.seano.steamosaic.api.model

import com.fasterxml.jackson.annotation.JsonProperty

data class SteamId(val success: Int, @field:JsonProperty("steamid") val steamId: String? = null)
