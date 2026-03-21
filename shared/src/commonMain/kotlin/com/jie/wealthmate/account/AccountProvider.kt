package com.jie.wealthmate.account

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccountProvider {
    private val _currentAccount = MutableStateFlow<String?>(null)
    val currentAccount: StateFlow<String?> = _currentAccount.asStateFlow()

    fun updateAccount(account: String?) {
        _currentAccount.value = account
    }

    fun isSpecialAccount(): Boolean =
        _currentAccount.value == "uohihi@gmail.com"
}