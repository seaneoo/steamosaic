package dev.seano.steamosaic.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

@Service
class ImageService {
    private val restClient = RestClient.create()
    private val maxBytes: Long = 100_000
    private val logger: Logger = LoggerFactory.getLogger(ImageService::class.java.name)

    fun fetchImageFromUrl(urlString: String): BufferedImage? {
        logger.info("Fetching image from URL: $urlString")

        val bytes =
            try {
                restClient.get().uri(urlString).retrieve().body<ByteArray>()
            } catch (_: Exception) {
                logger.warn("Failed to fetch image.")
                return null
            }

        if (bytes == null) {
            logger.warn("Failed to fetch image.")
            return null
        }

        if (bytes.size > maxBytes) {
            logger.warn("Image size exceeds the maximum allowed size of $maxBytes bytes.")
            return null
        }

        ByteArrayInputStream(bytes).use { bais ->
            val image = ImageIO.read(bais)
            return if (image == null) {
                logger.warn("Failed to decode image.")
                null
            } else {
                logger.info("Successfully fetched and decoded image of ${bytes.size} bytes.")
                image
            }
        }
    }
}
