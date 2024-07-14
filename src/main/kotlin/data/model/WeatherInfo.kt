package data.model

import java.time.LocalDateTime

data class WeatherInfo(
    val location: String,
    val condition: WeatherCondition,
    val requestTime: LocalDateTime
)
