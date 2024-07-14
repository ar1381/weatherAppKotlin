package data.api

import data.model.NetworkResult
import data.model.WeatherInfo

interface WeatherService {
    suspend fun getWeatherByCity(cityName: String): NetworkResult<WeatherInfo>
    suspend fun getWeatherByLatLong(lat: Float, long: Float): NetworkResult<WeatherInfo>
}
