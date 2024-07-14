package data.api

import data.model.NetworkResult
import data.model.WeatherCondition
import data.model.WeatherInfo
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class WeatherServiceImpl(private val apiKey: String) : WeatherService {
    private val client = HttpClient()

    override suspend fun getWeatherByCity(cityName: String): NetworkResult<WeatherInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val response: HttpResponse = client.get("https://api.weatherapi.com/v1/current.json") {
                    parameter("key", apiKey)
                    parameter("q", cityName)
                }
                val weatherInfo = parseResponse(response)
                NetworkResult.Success(weatherInfo)
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "An error occurred")
            }
        }
    }

    override suspend fun getWeatherByLatLong(lat: Float, long: Float): NetworkResult<WeatherInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val response: HttpResponse = client.get("https://api.weatherapi.com/v1/current.json") {
                    parameter("key", apiKey)
                    parameter("q", "$lat,$long")
                }
                val weatherInfo = parseResponse(response)
                NetworkResult.Success(weatherInfo)
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "An error occurred")
            }
        }
    }

    private fun parseResponse(response: HttpResponse): WeatherInfo {
        val condition = WeatherCondition("Sunny", 1000)
        return WeatherInfo("Sample Location", condition, LocalDateTime.now())
    }
}
