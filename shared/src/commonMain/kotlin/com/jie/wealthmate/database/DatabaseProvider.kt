package com.jie.wealthmate.database

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class DatabaseProvider(private val builder: DatabaseBuilder) {
    private var _database: AppDatabase = builder.build()
    val database: AppDatabase get() = _database

    private val _onDatabaseReplaced = MutableSharedFlow<Unit>()
    val onDatabaseReplaced = _onDatabaseReplaced.asSharedFlow()

    suspend fun refreshDatabase() {
        try {
            // isOpen 체크 없이 바로 close 호출 (Room에서 안전하게 처리됨)
            _database.close()
        } catch (e: Exception) {
            // 이미 닫혔거나 에러 발생 시 무시
        }

        // 새 인스턴스 생성
        _database = builder.build()
        // 변경 알림
        _onDatabaseReplaced.emit(Unit)
    }
}