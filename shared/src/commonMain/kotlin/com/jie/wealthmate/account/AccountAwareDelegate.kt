@file:OptIn(ExperimentalCoroutinesApi::class)

package com.jie.wealthmate.account

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class AccountAwareDelegate<T>(
    private val special: T,
    private val normal: T,
    private val accountProvider: AccountProvider,
) {
    val isComposite: Boolean
        get() = accountProvider.currentAccount.value == "uohihi@gmail.com"

    val current: T
        get() = if (accountProvider.isSpecialAccount()) special else normal

    // ✅ 쓰기용 - composite 계정이면 양쪽 모두 호출
    suspend fun dualCall(block: suspend (T) -> Unit) {
        if (isComposite) {
            block(normal)
            block(special)
        } else {
            block(current)
        }
    }

    // ✅ 반환값 있는 읽기용
    suspend fun <R> call(block: suspend (T) -> R): R {
        return block(current)
    }

    // ✅ 복원용 (Special -> Normal)
    suspend fun restore(block: suspend (special: T, normal: T) -> Unit) {
        if (isComposite) {
            block(special, normal)
        }
    }

    // Flow 읽기용 - composite이어도 special(firestore) 하나만 관찰
    fun <R> flatFlow(block: (T) -> Flow<R>): Flow<R> =
        accountProvider.currentAccount
            .map { account -> current }
            .flatMapLatest(block)

    // Flow 읽기용 - composite일 때 양쪽 merge가 필요하면 이걸 사용
    fun <R> mergeFlow(block: (T) -> Flow<R>): Flow<R> =
        accountProvider.currentAccount
            .flatMapLatest { account ->
                if (account == "uohihi@gmail.com") {
                    merge(block(normal), block(special))
                } else {
                    block(if (accountProvider.isSpecialAccount()) special else normal)
                }
            }
}
