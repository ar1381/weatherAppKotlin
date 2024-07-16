package ui

import app.WeatherApplication
import data.service.WeatherServiceImpl
import data.storage.HistoryStorageImpl
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.Dispatchers
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json



fun main() {
    val apiKey = "bc5f571495d04538ac570008241407"
    val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 15000 // 15 seconds
            connectTimeoutMillis = 10000 // 10 seconds
            socketTimeoutMillis = 10000 // 10 seconds
        }
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
//                    println(message)
                }

            }
        }
        install(ContentNegotiation) {
            json(

            )
        }

    }
    val dispatcher = Dispatchers.IO
    val weatherService = WeatherServiceImpl(apiKey, client, dispatcher)
    val historyStorage = HistoryStorageImpl("history.json", dispatcher)
    val app = WeatherApplication(weatherService, historyStorage)


    while (true) {
        print("Enter command: ")
        val command = readLine() ?: break
        app.executeCommand(command)
    }

}