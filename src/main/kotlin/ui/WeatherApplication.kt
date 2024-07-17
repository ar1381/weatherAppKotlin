package app

import data.api.WeatherService
import data.model.NetworkResult
import data.api.WeatherServiceImpl
import data.model.WeatherInfo
import data.storage.HistoryStorage
import kotlinx.coroutines.*

class WeatherApplication(
    private val weatherService: WeatherService,
    private val historyStorage: HistoryStorage,
) {
    fun executeCommand(command: String) {
        val parts = command.split(" ")
        when (parts[0].lowercase()) {
            "getweather" -> {
                if (parts.size == 2) {
                    val location = parts[1]
                    runBlocking {
                        withLoadingBar {
                            val result = if (location.contains(",")) {
                                val (lat, long) = location.split(",").map { it.toFloat() }
                                weatherService.getWeatherByLatLong(lat, long)
                            } else {
                                weatherService.getWeatherByCity(location)
                            }
                            handleResult(result)
                        }
                    }
                } else if (parts.size == 1 && parts[0] == "getweather") {
                    runBlocking {
                        withLoadingBar {
                            val result = (weatherService as WeatherServiceImpl).getWeatherByIP()
                            handleResult(result)
                        }
                    }
                } else {
                    println("Invalid command. Usage: getweather <city_name> or <lat,long>")
                }
            }
            "history" -> {
                runBlocking {
                    val query = if (parts.size == 2) parts[1] else null
                    val history = historyStorage.getSearchHistory(query)
                    println("Search History:")
                    history.forEach { println(it) }
                }
            }
            else -> {
                println("Unknown command.")
            }
        }
    }

    private suspend fun handleResult(result: NetworkResult<WeatherInfo>) {
        when (result) {
            is NetworkResult.Success -> {
                println("Weather Info: location = ${result.data.location}, condition = ${result.data.condition}, time = ${result.data.requestTime}")
                historyStorage.saveSearch(result.data)
            }
            is NetworkResult.Error -> {
                println("Error: ${result.exception}")
            }
        }
    }
    suspend fun withLoadingBar(block: suspend () -> Unit) = coroutineScope {
        val job = launch {
            var progress = 0
            val length = 10
            while (isActive) {
                val bar = "[" + "#".repeat(progress) + " ".repeat(length - progress) + "]"
                print("\rLoading $bar")
                delay(100)
                progress = (progress + 1) % (length + 1)
            }
        }

        try {
            block()
        } finally {
            job.cancelAndJoin()
            println("\rLoading [##########] Done!")
        }
    }
}
