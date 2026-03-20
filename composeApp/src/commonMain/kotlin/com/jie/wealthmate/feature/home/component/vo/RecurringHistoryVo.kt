package com.jie.wealthmate.feature.home.component.vo

import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum

data class RecurringHistoryVo(
    val id: String,
    val categoryIcon: String,
    val largeCategory: LargeCategoryEnum,
    val content: String,
    val singleAmount: Long,
    val monthlyTotalAmount: Long,
    val recurringDateText: String,
    val isPassed: Boolean,
    val isToday: Boolean = false,
    val isFixed: Boolean = false,
    val sortOrder: Int = 0,
)