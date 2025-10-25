package dev.seano.steamosaic.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.sqrt

@Service
class MosaicService(
    private val steamService: SteamService,
    private val imageService: ImageService,
) {
    private val logger: Logger = LoggerFactory.getLogger(MosaicService::class.java.name)

    private fun buildMosaicImage(images: List<BufferedImage>): ByteArray? {
        val n = images.size
        val sq = ceil(sqrt(n.toDouble())).toInt()
        val width = images.maxOf { it.width }
        val height = images.maxOf { it.height }
        val totalWidth = sq * width
        val totalHeight = sq * height

        val baos = ByteArrayOutputStream()
        val outImage = BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB)
        val g2d = outImage.createGraphics()

        try {
            g2d.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR,
            )
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            g2d.color = Color(1f, 0f, 0f, 0f)
            g2d.fillRect(0, 0, totalWidth, totalHeight)

            images.forEachIndexed { idx, image ->
                val row = idx / sq
                val col = idx % sq
                val x = col * width
                val y = row * height
                g2d.drawImage(image, x, y, null)
            }

            return if (!ImageIO.write(outImage, "png", baos)) {
                logger.warn("Failed to write image to byte array.")
                null
            } else {
                baos.toByteArray()
            }
        } catch (e: Exception) {
            logger.warn("Error generating mosaic image: ${e.message}")
            g2d.dispose()
            return null
        }
    }

    fun buildMosaic(steamId: String): ByteArray? {
        logger.info("Generating image for Steam ID: $steamId")

        val appIds =
            steamService
                .fetchOwnedGames(steamId)
                .response
                .games
                .sortedWith { a, b -> b.playtime.compareTo(a.playtime) }
                .map { it.appId }
        logger.info("Found ${appIds.size} owned games.")

        val imageAssets =
            steamService.fetchGameAssets(appIds.take(9).map { it.toString() }.toTypedArray()).map {
                Pair(it.appId, it.assets.smallCapsule)
            }
        val imageUrls =
            imageAssets.map { steamService.getHeaderImageUrl(it.first.toString(), it.second) }
        val images = imageUrls.mapNotNull { imageService.fetchImageFromUrl(it) }

        val imageGrid =
            buildMosaicImage(images)
                ?: throw ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to generate mosaic image.",
                )
        logger.info("Generated image grid of size ${imageGrid.size} bytes.")
        return imageGrid
    }
}
