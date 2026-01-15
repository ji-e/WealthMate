package com.jie.wealthmate.database


const val DATABASE_NAME = "wealthmate.db"

class Database(
    databaseDriverFactory: DatabaseDriverFactory,
) {
    private val database = WMDatabase(driver = databaseDriverFactory.createDriver())
    private val dbQuery = database.transactionQueries

    // todo temp

    /**
     * 모든 거래 내역을 가져옵니다.
     * @return 거래 내역 리스트 (Transaction은 SQLDelight가 생성한 데이터 클래스)
     */
    fun getAllTransactions(): List<Transaction> {
        return dbQuery.selectAll().executeAsList()
    }

    /**
     * 새로운 거래 내역을 데이터베이스에 추가합니다.
     * @param title 거래 제목
     * @param amount 거래 금액
     * @param createdAt 생성 타임스탬프 (밀리초)
     */
    fun insertTransaction(
        title: String,
        amount: Double,
        createdAt: Long,
    ) {
        // SQLDelight가 생성한 함수는 'created_at'이 아닌 'createdAt' 파라미터를 사용합니다.
        dbQuery.insert(
            title = title,
            amount = amount,
            createdAt = createdAt // 'created_at' -> 'createdAt'으로 수정
        )
    }

    /**
     * 특정 ID의 거래 내역을 삭제합니다.
     * @param id 삭제할 거래 내역의 ID
     */
    fun deleteTransactionById(id: Long) {
        dbQuery.deleteById(id = id)
    }
}