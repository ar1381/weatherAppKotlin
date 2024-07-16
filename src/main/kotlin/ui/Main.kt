package ui

import app.WeatherApplication
import data.api.WeatherServiceImpl
import data.storage.HistoryStorageImpl

fun main() {
    val apiKey = "bc5f571495d04538ac570008241407"
    val weatherService = WeatherServiceImpl(apiKey)
    val historyStorage = HistoryStorageImpl("history.json")
    val app = WeatherApplication(weatherService, historyStorage)

    while (true) {
        print("Enter command: ")
        val command = readLine() ?: break
        app.executeCommand(command)
    }

}