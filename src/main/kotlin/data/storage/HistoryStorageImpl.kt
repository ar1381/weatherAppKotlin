package data.storage

import data.model.WeatherCondition
import data.model.WeatherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class HistoryStorageImpl(private val file: File) : HistoryStorage {
    private val lock = ReentrantLock()

    override suspend fun saveSearch(weatherInfo: WeatherInfo) {
        withContext(Dispatchers.IO) {
            lock.withLock {
                file.appendText(weatherInfo.toString() + "\n")
            }
        }
    }

    override suspend fun getSearchHistory(query: String?): List<WeatherInfo> {
        return withContext(Dispatchers.IO) {
            lock.withLock {
                val history = file.readLines().map { line ->
                    //tehran is a sample location
                    WeatherInfo("tehran", WeatherCondition("Sunny", 1000), LocalDateTime.now())
                }
                if (query == null) {
                    history
                } else {
                    history.filter { it.location.contains(query, ignoreCase = true) }
                }
            }
        }
    }
}
