package data.storage

import data.model.WeatherInfo

interface HistoryStorage {
    suspend fun saveSearch(weatherInfo: WeatherInfo)
    suspend fun getSearchHistory(query: String?): List<WeatherInfo>
}
