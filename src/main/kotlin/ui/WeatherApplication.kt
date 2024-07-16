package app

import kotlinx.coroutines.runBlocking
import data.api.WeatherService
import data.model.NetworkResult
import data.storage.HistoryStorage

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
                        val result = if (location.contains(",")) {
                            val (lat, long) = location.split(",").map { it.toFloat() }
                            weatherService.getWeatherByLatLong(lat, long)
                        } else {
                            weatherService.getWeatherByCity(location)
                        }

                        when (result) {
                            is NetworkResult.Success -> {
                                println("Weather Info: location = ${result.data.location}, condition = ${result.data.condition}, time = ${result.data.requestTime}")
                                historyStorage.saveSearch(result.data)
                            }
                            is NetworkResult.Error -> {
                                println("Error: ${result.exception}")
                                println("Error 2")
                            }
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
}
