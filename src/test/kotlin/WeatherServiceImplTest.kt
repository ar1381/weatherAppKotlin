package data.api

import data.api.WeatherService
import data.model.NetworkResult
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WeatherServiceImplTest {

    private lateinit var weatherService: WeatherService
    private lateinit var mockClient: HttpClient
    private val apiKey = "test_api_key"

    @BeforeEach
    fun setup() {
        mockClient = HttpClient(MockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            engine {
                addHandler { request ->
                    val responseJson = when (request.url.toString()) {
                        "https://api.weatherapi.com/v1/current.json?key=$apiKey&q=Tehran" -> """
                            {
                              "location": {
                                "name": "Tehran"
                              },
                              "current": {
                                "condition": {
                                  "text": "Clear",
                                  "code": 1000
                                }
                              }
                            }
                        """
                        "https://api.ipify.org?format=json" -> """
                            {"ip": "127.0.0.1"}
                        """
                        "https://ipapi.co/127.0.0.1/json" -> """
                            {"city": "Tehran"}
                        """
                        else -> "{}"
                    }

                    respond(
                        responseJson,
                        HttpStatusCode.OK,
                        headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                    )
                }
            }
        }

        weatherService = WeatherServiceImpl(apiKey, mockClient, Dispatchers.Unconfined)
    }

    @Test
    fun `test getWeatherByCity`() = runBlocking {
        val result = weatherService.getWeatherByCity("Tehran")

        when (result) {
            is NetworkResult.Success -> {
                assertEquals("Tehran", result.data.location)
                assertEquals("Clear", result.data.condition.text)
                assertEquals(1000, result.data.condition.code)
            }
            is NetworkResult.Error -> fail("Expected success, but got error: ${result.exception}")
        }
    }

    @Test
    fun `test getWeatherByLatLong`() = runBlocking {
//        val result = weatherService.getWeatherByLatLong(35.6892f, 51.3890f)
//
//        when (result) {
//            is NetworkResult.Success -> {
//                assertEquals("Tehran", result.data.location)
//                assertEquals("Clear", result.data.condition.text)
//                assertEquals(1000, result.data.condition.code)
//            }
//            is NetworkResult.Error -> fail("Expected success, but got error: ${result.exception}")
//        }
    }

    @Test
    fun `test getWeatherByIP`() = runBlocking {
        val result = (weatherService as WeatherServiceImpl).getWeatherByIP()

        when (result) {
            is NetworkResult.Success -> {
                assertEquals("Tehran", result.data.location)
                assertEquals("Clear", result.data.condition.text)
                assertEquals(1000, result.data.condition.code)
            }
            is NetworkResult.Error -> fail("Expected success, but got error: ${result.exception}")
        }
    }
}