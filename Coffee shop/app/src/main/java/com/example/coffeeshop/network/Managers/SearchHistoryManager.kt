package com.example.coffeeshop.network.Managers

import android.content.Context

class SearchHistoryManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val maxHistorySize = 4
    private val historyKey = "search_history_items"

    fun addSearchQuery(query: String) {
        if (query.isBlank()) return

        val currentHistory = getSearchHistory().toMutableList()

        currentHistory.removeAll { it.equals(query, ignoreCase = true) }
        currentHistory.add(0, query)
        if (currentHistory.size > maxHistorySize) {
            currentHistory.subList(maxHistorySize, currentHistory.size).clear()
        }

        sharedPreferences.edit()
            .putString(historyKey, currentHistory.joinToString("|||"))
            .apply()
    }

    fun getSearchHistory(): List<String> {
        val historyString = sharedPreferences.getString(historyKey, "")
        return if (historyString.isNullOrEmpty()) {
            emptyList()
        } else {
            historyString.split("|||").filter { it.isNotBlank() }
        }
    }

    fun clearSearchHistory() {
        sharedPreferences.edit()
            .remove(historyKey)
            .apply()
    }
}