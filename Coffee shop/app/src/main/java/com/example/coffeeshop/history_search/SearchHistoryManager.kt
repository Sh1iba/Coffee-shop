package com.example.coffeeshop.history_search

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class SearchHistoryManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val maxHistorySize = 10
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
            .putStringSet(historyKey, currentHistory.toSet())
            .apply()
    }

    fun getSearchHistory(): List<String> {
        return sharedPreferences.getStringSet(historyKey, emptySet())?.toList() ?: emptyList()
    }

    fun clearSearchHistory() {
        sharedPreferences.edit()
            .remove(historyKey)
            .apply()
    }
}