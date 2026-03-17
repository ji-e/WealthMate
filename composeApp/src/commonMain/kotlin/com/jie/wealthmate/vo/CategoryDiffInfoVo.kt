package com.jie.wealthmate.vo

data class CategoryDiffInfoVo(
    val categoryIcon: String,
    val categoryName: String,
    val currentAmount: Long,
    val diffAmount: Long,
    val ratio: Float = 0f
) {
    val lastAmount: Long = currentAmount - diffAmount
}
