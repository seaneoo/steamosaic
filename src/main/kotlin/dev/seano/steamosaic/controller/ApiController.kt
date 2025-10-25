package dev.seano.steamosaic.controller

import dev.seano.steamosaic.model.BuildMosaicDto
import dev.seano.steamosaic.service.SteamService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api")
class ApiController(private val steamService: SteamService) {
    @GetMapping("/steam-id/{name}", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun fetchSteamId(@PathVariable name: String): ResponseEntity<String?> {
        val steamId = steamService.fetchSteamId(name).response.steamId
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(steamId)
    }

    @PostMapping("/mosaic", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun buildMosaic(@ModelAttribute data: BuildMosaicDto, httpResponse: HttpServletResponse) {
        val identifier =
            steamService.getSteamId(data.identifier)
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        httpResponse.sendRedirect("/${identifier}.png")
    }
}
