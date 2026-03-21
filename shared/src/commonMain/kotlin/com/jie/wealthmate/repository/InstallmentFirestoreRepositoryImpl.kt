package com.jie.wealthmate.repository

import com.jie.wealthmate.database.eneity.InstallmentEntity
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock

class InstallmentFirestoreRepositoryImpl(
    private val authRepository: AuthRepository
) : InstallmentRepository {
    private val repoName = "InstallmentFirestoreRepository"
    private val firestore = Firebase.firestore

    private fun getUserId(): String = authRepository.getUserName() ?: "anonymous"
    private fun getInstallmentCollection() = firestore
        .collection("users")
        .document(getUserId())
        .collection("installments")

    override suspend fun insertInstallment(installment: InstallmentEntity) = withContext(Dispatchers.Default) {
        val now = Clock.System.now().toEpochMilliseconds()
        getInstallmentCollection().document(installment.id).set(installment.copy(updatedAt = now), encodeDefaults = true)
    }

    override suspend fun updateInstallment(installment: InstallmentEntity) = withContext(Dispatchers.Default) {
        val now = Clock.System.now().toEpochMilliseconds()
        getInstallmentCollection().document(installment.id).set(installment.copy(updatedAt = now), encodeDefaults = true)
    }

    override suspend fun deleteInstallment(installmentId: String) = withContext(Dispatchers.Default) {
        val docRef = getInstallmentCollection().document(installmentId)
        val snapshot = docRef.get()
        if (snapshot.exists) {
            val current = snapshot.data<InstallmentEntity>()
            docRef.set(current.copy(isDeleted = true, updatedAt = Clock.System.now().toEpochMilliseconds()), encodeDefaults = true)
        }
    }

    override fun getInstallments(): Flow<List<InstallmentEntity>> =
        getInstallmentCollection()
            .where { "isDeleted" equalTo false }
            .snapshots
            .map { snapshot -> snapshot.documents.map { it.data<InstallmentEntity>() } }
            .flowOn(Dispatchers.Default)
}
