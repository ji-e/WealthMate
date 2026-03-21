package com.jie.wealthmate.account

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class AccountAwareDelegate<T>(
    private val special: T,
    private val normal: T,
    private val accountProvider: AccountProvider,
) {
    // 단발성 호출용
    val current: T
        get() = if (accountProvider.isSpecialAccount()) special else normal

    // Flow 관찰용
    fun <R> flatFlow(block: (T) -> Flow<R>): Flow<R> =
        accountProvider.currentAccount
            .map { account ->

                Napier.e("account: $account")
                if (account == "uohihi@gmail.com") special else normal
            }
            .flatMapLatest(block)
}