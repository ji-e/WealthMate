package com.jie.wealthmate.feature.home.component.vo

data class RecurringInfoVo(
    val isPassed: Boolean,
    val isToday: Boolean,
    val dateText: String,
    val sortOrder: Int,
)