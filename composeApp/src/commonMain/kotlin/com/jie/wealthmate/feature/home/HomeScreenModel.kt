package com.jie.wealthmate.feature.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.jie.wealthmate.database.Database
import com.jie.wealthmate.database.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeScreenModel(  private val database: Database) : ScreenModel {
    private val _counter = MutableStateFlow(0)
    val counter = _counter.asStateFlow()
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions = _transactions.asStateFlow()
    private val screenScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    init {
        loadAllTransactions()
    }

    private fun loadAllTransactions() {
        // ViewModel의 viewModelScope는 자체적으로 백그라운드 스레드에서 실행됩니다.
        screenScope.launch {
            // SQLDelight는 기본적으로 백그라운드 스레드에서 쿼리를 실행하도록 설정할 수 있습니다.
            val transactionList = database.getAllTransactions()
            _transactions.value = transactionList

            println(transactionList)
        }
    }

    fun addTransaction(
        title: String,
        amount: Double
    ) {
        screenScope.launch {
            database.insertTransaction(
                title = title,
                amount = amount,
                createdAt = counter.value.toLong()
            )
            loadAllTransactions()
        }
    }

    fun increment() {
        screenModelScope.launch {
            _counter.value++
        }
    }
}