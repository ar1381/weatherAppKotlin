package data.model

import data.api.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class WeatherInfo(
    val location: String,
    val condition: WeatherCondition,
    @Serializable(with = LocalDateTimeSerializer::class) val requestTime: LocalDateTime
)
