package com.example.coffeeshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.managers.SearchHistoryManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchHistoryManager: SearchHistoryManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchState())
    val uiState: StateFlow<SearchState> = _uiState.asStateFlow()

    init {
        loadSearchHistory()
    }

    fun onSearchTextChange(text: String) {
        _uiState.value = _uiState.value.copy(searchText = text)
    }

    fun onSearchFocusChange(isFocused: Boolean) {
        _uiState.value = _uiState.value.copy(isSearchFocused = isFocused)
        if (isFocused && _uiState.value.searchText.isEmpty()) {
            loadSearchHistory()
        }
    }

    fun performSearch(onSearch: (String) -> Unit) {
        val currentText = _uiState.value.searchText
        if (currentText.isNotBlank()) {
            searchHistoryManager.addSearchQuery(currentText)
            loadSearchHistory()
            onSearch(currentText)
        }
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(searchText = "")
    }

    fun clearSearchHistory() {
        searchHistoryManager.clearSearchHistory()
        loadSearchHistory()
    }

    fun selectSearchHistoryItem(item: String, onSearch: (String) -> Unit) {
        _uiState.value = _uiState.value.copy(searchText = item)
        searchHistoryManager.addSearchQuery(item)
        loadSearchHistory()
        onSearch(item)
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                searchHistory = searchHistoryManager.getSearchHistory()
            )
        }
    }
}

data class SearchState(
    val searchText: String = "",
    val searchHistory: List<String> = emptyList(),
    val isSearchFocused: Boolean = false
)