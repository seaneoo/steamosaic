package dev.seano.steamosaic.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.sqrt

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

    fun buildGrid(images: List<BufferedImage>): ByteArray {
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

            if (!ImageIO.write(outImage, "png", baos)) {
                throw RuntimeException("Failed to write image to byte array.")
            }
            return baos.toByteArray()
        } catch (e: Exception) {
            g2d.dispose()
            throw e
        }
    }
}
