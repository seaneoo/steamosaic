package dev.seano.steamosaic.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

@Service
class ImageService {
    private val restClient = RestClient.create()
    private val maxBytes: Long = 100_000
    private val logger: Logger = LoggerFactory.getLogger(ImageService::class.java.name)

    companion object {
        const val DEFAULT_WIDTH = 231

        const val DEFAULT_HEIGHT = 87
    }

    private fun resize(
        image: BufferedImage,
        width: Int = DEFAULT_WIDTH,
        height: Int = DEFAULT_HEIGHT,
    ): BufferedImage {
        val temp = image.getScaledInstance(width, height, Image.SCALE_SMOOTH)
        val resized = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g2d = resized.createGraphics()
        g2d.drawImage(temp, 0, 0, null)
        g2d.dispose()
        return resized
    }

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
                if (image.width != DEFAULT_WIDTH || image.height != DEFAULT_HEIGHT) {
                    logger.info(
                        "Resizing image from ${image.width}x${image.height} to ${DEFAULT_WIDTH}x${DEFAULT_HEIGHT}."
                    )
                    resize(image)
                } else {
                    image
                }
            }
        }
    }
}
