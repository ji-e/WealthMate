package com.jie.wealthmate.feature.menu

import com.jie.wealthmate.base.BaseUiState

data class MenuUiState(
  val menuItems:List<MenuItem> = emptyList()
) : BaseUiState