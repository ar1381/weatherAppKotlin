package data.service

import data.api.WeatherService
import data.model.NetworkResult
import data.model.WeatherCondition
import data.model.WeatherInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

class WeatherServiceImpl(
    private val apiKey:String,
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher
) : WeatherService {
    val apiurl = "https://api.weatherapi.com/v1/current.json"
    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun getWeatherByCity(cityName: String): NetworkResult<WeatherInfo> {
        return withContext(dispatcher) {
            try {
                val response: HttpResponse = client.get(apiurl) {
                    parameter("key", apiKey)
                    parameter("q", cityName)
                }.body()
                val weatherInfo = parseResponse(response)
                NetworkResult.Success(weatherInfo)
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "An error occurred")
            }
        }
    }

    override suspend fun getWeatherByLatLong(lat: Float, long: Float): NetworkResult<WeatherInfo> {
        return withContext(dispatcher) {
            try {
                val response: HttpResponse = client.get(apiurl) {
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
    suspend fun getWeatherByIP(): NetworkResult<WeatherInfo> {
        return withContext(dispatcher) {
            try {
                val ipResponse: HttpResponse = client.get("https://api.ipify.org?format=json")
                val ipResponseBody = ipResponse.body<String>()
                val ip = json.decodeFromString<IpResponse>(ipResponseBody).ip

                val locationResponse: HttpResponse = client.get("https://ipapi.co/$ip/json")
                val locationResponseBody = locationResponse.body<String>()
                val location = json.decodeFromString<LocationResponse>(locationResponseBody)
                println(location)

                return@withContext getWeatherByCity(location.city ?: "Tehran")
            } catch (e: Exception) {
                NetworkResult.Error(e.message ?: "An error occurred")
            }
        }
    }

    private suspend fun parseResponse(response: HttpResponse): WeatherInfo {
        // Parse the response JSON to create a WeatherInfo object
        val jsonResponse = response.body<String>()
        val json = kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
        }
        val weatherApiResponse = json.decodeFromString(WeatherApiResponse.serializer(), jsonResponse)

        val condition = WeatherCondition(
            text = weatherApiResponse.current.condition.text,
            code = weatherApiResponse.current.condition.code
        )

        return WeatherInfo(
            location = weatherApiResponse.location.name,
            condition = condition,
            requestTime = LocalDateTime.now()
        )
    }
}

@Serializable
data class WeatherApiResponse(
    val location: Location,
    val current: Current
) {
    @Serializable
    data class Location(
        val name: String
    )

    @Serializable
    data class Current(
        val condition: WeatherCondition
    )
}

@Serializable
data class WeatherCondition(
    val text: String,
    val code: Int
)

@Serializable
data class IpResponse(val ip: String)

@Serializable
data class LocationResponse(val city: String?)