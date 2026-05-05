package com.example.coffeeshop.domain

import java.math.BigDecimal

data class ProductManageRequest(
    val name: String,
    val description: String,
    val categoryId: Int,
    val imageName: String,
    val variants: List<VariantRequest>
)

data class VariantRequest(
    val size: String,
    val price: BigDecimal
)
