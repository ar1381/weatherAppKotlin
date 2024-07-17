package data.api

import data.model.NetworkResult
import data.model.WeatherCondition
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class WeatherServiceImplTest {

    private val apiKey = "test-api-key"
    private val dispatcher = Dispatchers.IO
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `test getWeatherByCity success`() = runTest {
        val mockEngine = MockEngine { request ->
            respond(
                content = json.encodeToString(
                    WeatherApiResponse(
                        location = WeatherApiResponse.Location("Tehran"),
                        current = WeatherApiResponse.Current(
                            condition = WeatherCondition("Clear", 1000)
                        )
                    )
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val weatherService = WeatherServiceImpl(apiKey, client, dispatcher)
        val result = weatherService.getWeatherByCity("Tehran")

        assertTrue(result is NetworkResult.Success)
        result as NetworkResult.Success
        assertEquals("Tehran", result.data.location)
        assertEquals("Clear", result.data.condition.text)
    }

    @Test
    fun `test getWeatherByCity error`() = runTest {
        val mockEngine = MockEngine { request ->
            respond(
                content = "Not Found",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Plain.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val weatherService = WeatherServiceImpl(apiKey, client, dispatcher)
        val result = weatherService.getWeatherByCity("UnknownCity")

        assertTrue(result is NetworkResult.Error)
    }

    @Test
    fun `test getWeatherByLatLong success`() = runTest {
        val mockEngine = MockEngine { request ->
            respond(
                content = json.encodeToString(
                    WeatherApiResponse(
                        location = WeatherApiResponse.Location("Tehran"),
                        current = WeatherApiResponse.Current(
                            condition = WeatherCondition("Clear", 1000)
                        )
                    )
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val weatherService = WeatherServiceImpl(apiKey, client, dispatcher)
        val result = weatherService.getWeatherByLatLong(35.6892f, 51.389f)

        assertTrue(result is NetworkResult.Success)
        result as NetworkResult.Success
        assertEquals("Tehran", result.data.location)
        assertEquals("Clear", result.data.condition.text)
    }

    @Test
    fun `test getWeatherByIP success`() = runTest {
        val mockEngine = MockEngine { request ->
            when (request.url.toString()) {
                "https://api.ipify.org?format=json" -> respond(
                    content = json.encodeToString(IpResponse("204.12.192.219")),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
                "https://ipapi.co/204.12.192.219/json" -> respond(
                    content = json.encodeToString(LocationResponse("Tehran", "Tehran", "Iran",
                        35.6892f.toString(), 51.389f)),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
                "https://api.weatherapi.com/v1/current.json?key=test-api-key&q=Tehran" -> respond(
                    content = json.encodeToString(
                        WeatherApiResponse(
                            location = WeatherApiResponse.Location("Tehran"),
                            current = WeatherApiResponse.Current(
                                condition = WeatherCondition("Clear", 1000)
                            )
                        )
                    ),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
                else -> respond(
                    content = "Not Found",
                    status = HttpStatusCode.NotFound,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Plain.toString())
                )
            }
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val weatherService = WeatherServiceImpl(apiKey, client, dispatcher)
        val result = weatherService.getWeatherByIP()

        assertTrue(result is NetworkResult.Success)
        result as NetworkResult.Success
        assertEquals("Tehran", result.data.location)
        assertEquals("Clear", result.data.condition.text)
    }
}