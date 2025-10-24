package dev.seano.steamosaic

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class SteamosaicApplication

fun main(args: Array<String>) {
    runApplication<SteamosaicApplication>(*args)
}
