package com.example.coffeeshop.data.remote.response

data class PagedProductResponse(
    val content: List<ProductResponse>,
    val currentPage: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val isLast: Boolean
)
