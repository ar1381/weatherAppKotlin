package ui

import data.api.WeatherServiceImpl

fun main() {
    val apiKey = "bc5f571495d04538ac570008241407"
    val weatherService = WeatherServiceImpl(apiKey)

}