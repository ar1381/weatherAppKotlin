package data.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherCondition(
    val text: String,
    val code: Int
)
