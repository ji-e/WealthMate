package com.jie.wealthmate.entity

data class CategoryTagEntity(
    val id: Long,
    val tagLabel: String
)

data class CategoryEntity(
    val id: Long,
    val icon: String,
    val largeCategory: String,
    val middleLabel: String,
    val tags: List<CategoryTagEntity>
)
