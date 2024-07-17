package data.storage

import data.model.WeatherInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import java.io.File
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlinx.serialization.json.Json


class HistoryStorageImpl(private val filePath: String,
                         private val dispatcher: CoroutineDispatcher
) : HistoryStorage {
    private val lock = ReentrantLock()
    private val file = File(filePath)

    init {
        if (!file.exists()) {
            file.createNewFile()
            file.writeText("[]")
        }
    }

    override suspend fun saveSearch(weatherInfo: WeatherInfo) {
        val history = withContext(dispatcher){
            lock.withLock {
                Json.decodeFromString<List<WeatherInfo>>(file.readText()).toMutableList()
            }
        }
        history.add(weatherInfo)
        withContext(dispatcher){
            lock.withLock {
                file.writeText(Json.encodeToString(history))
            }
        }
    }

    override suspend fun getSearchHistory(query: String?): List<WeatherInfo> {
        return withContext(dispatcher) {
            lock.withLock {
                val history: List<WeatherInfo> = Json.decodeFromString(file.readText())
                return@withLock if (query.isNullOrBlank()) {
                    history
                } else {
                    history.filter { it.location.contains(query, ignoreCase = true) }
                }
            }
        }
    }
}