package com.jie.wealthmate.repository

import com.benasher44.uuid.uuid4
import com.jie.wealthmate.database.eneity.PaymentMethodEntity
import com.jie.wealthmate.database.eneity.PaymentMethodGroupEntity
import com.jie.wealthmate.database.eneity.PaymentMethodWithGroupEntity
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock

class PaymentMethodFirestoreRepositoryImpl(
    private val authRepository: AuthRepository
) : PaymentMethodRepository {
    private val firestore = Firebase.firestore

    private fun generateId(): String = uuid4().toString()
    private fun getUserId(): String = authRepository.getUserName() ?: "anonymous"
    
    private fun getPaymentMethodCollection() = firestore
        .collection("users")
        .document(getUserId())
        .collection("payment_methods")

    private fun getGroupCollection() = firestore
        .collection("users")
        .document(getUserId())
        .collection("payment_method_groups")

    override suspend fun insertPaymentMethod(
        paymentMethodLabel: String,
        paymentMethodGroupId: String?,
        paymentMethodGroupLabel: String?,
        sort: Long
    ) = withContext(Dispatchers.Default) {
        val id = generateId()
        val now = Clock.System.now().toEpochMilliseconds()
        val entity = PaymentMethodEntity(
            id = id,
            label = paymentMethodLabel,
            groupId = paymentMethodGroupId,
            groupLabel = paymentMethodGroupLabel,
            sort = sort,
            updatedAt = now,
            isDeleted = false
        )
        getPaymentMethodCollection().document(id).set(entity, encodeDefaults = true)
    }

    override suspend fun updatePaymentMethod(paymentMethod: PaymentMethodEntity) = withContext(Dispatchers.Default) {
        val updated = paymentMethod.copy(updatedAt = Clock.System.now().toEpochMilliseconds())
        getPaymentMethodCollection().document(paymentMethod.id).set(updated, encodeDefaults = true)
    }

    override suspend fun updatePaymentMethodSort(updates: List<Pair<String, Long>>) = withContext(Dispatchers.Default) {
        firestore.runTransaction {
            val snapshotsWithNewSort = updates.map { (id, newSort) ->
                val docRef = getPaymentMethodCollection().document(id)
                Triple(docRef, get(docRef), newSort)
            }
            val now = Clock.System.now().toEpochMilliseconds()
            snapshotsWithNewSort.forEach { (docRef, snapshot, newSort) ->
                if (snapshot.exists) {
                    val current = snapshot.data<PaymentMethodEntity>()
                    set(docRef, current.copy(sort = newSort, updatedAt = now), encodeDefaults = true)
                }
            }
        }
    }

    override suspend fun deletePaymentMethod(paymentMethodId: String) = withContext(Dispatchers.Default) {
        val docRef = getPaymentMethodCollection().document(paymentMethodId)
        val snapshot = docRef.get()
        if (snapshot.exists) {
            val current = snapshot.data<PaymentMethodEntity>()
            docRef.set(current.copy(isDeleted = true, updatedAt = Clock.System.now().toEpochMilliseconds()), encodeDefaults = true)
        }
    }

    override suspend fun getPaymentMethodById(paymentMethodId: String): PaymentMethodWithGroupEntity? = withContext(Dispatchers.Default) {
        val snapshot = getPaymentMethodCollection().document(paymentMethodId).get()
        if (!snapshot.exists) return@withContext null
        val entity = snapshot.data<PaymentMethodEntity>()
        val group = entity.groupId?.let { groupId ->
            getGroupCollection().document(groupId).get().takeIf { it.exists }?.data<PaymentMethodGroupEntity>()
        }
        PaymentMethodWithGroupEntity(entity, group)
    }

    override fun getPaymentMethods(): Flow<List<PaymentMethodWithGroupEntity>> =
        combine(
            getPaymentMethodCollection()
                .where { "isDeleted" equalTo false }
                .orderBy("sort", Direction.ASCENDING)
                .snapshots
                .map { snapshot -> snapshot.documents.map { it.data<PaymentMethodEntity>() } },
            getPaymentMethodGroups()
        ) { methods, groups ->
            methods.map { method ->
                PaymentMethodWithGroupEntity(
                    paymentMethod = method,
                    group = groups.find { it.id == method.groupId }
                )
            }
        }.flowOn(Dispatchers.Default)

    override suspend fun insertPaymentMethodGroup(label: String) = withContext(Dispatchers.Default) {
        val id = generateId()
        val now = Clock.System.now().toEpochMilliseconds()
        val entity = PaymentMethodGroupEntity(id = id, label = label, updatedAt = now)
        getGroupCollection().document(id).set(entity, encodeDefaults = true)
    }

    override suspend fun updatePaymentMethodGroup(paymentMethodGroupId: String, paymentMethodGroupLabel: String): Unit = withContext(Dispatchers.Default) {
        val now = Clock.System.now().toEpochMilliseconds()
        // Firestore transactions cannot execute queries. Fetch documents outside first.
        val methodsSnapshot = getPaymentMethodMethodQueryByGroupId(paymentMethodGroupId).get()
        
        firestore.runTransaction {
            // 1. Update group info
            val groupRef = getGroupCollection().document(paymentMethodGroupId)
            set(groupRef, PaymentMethodGroupEntity(id = paymentMethodGroupId, label = paymentMethodGroupLabel, updatedAt = now), encodeDefaults = true)
            
            // 2. Update all payment methods using this group
            methodsSnapshot.documents.forEach { doc ->
                val method = doc.data<PaymentMethodEntity>()
                set(doc.reference, method.copy(groupLabel = paymentMethodGroupLabel, updatedAt = now), encodeDefaults = true)
            }
        }
    }
    
    private fun getPaymentMethodMethodQueryByGroupId(groupId: String) = 
        getPaymentMethodCollection().where { "groupId" equalTo groupId }

    override suspend fun deletePaymentMethodGroup(paymentMethodGroupId: String): Unit = withContext(Dispatchers.Default) {
        val now = Clock.System.now().toEpochMilliseconds()
        // Firestore transactions cannot execute queries. Fetch documents outside first.
        val methodsSnapshot = getPaymentMethodMethodQueryByGroupId(paymentMethodGroupId).get()
        
        firestore.runTransaction {
            // 1. Soft delete group
            val groupRef = getGroupCollection().document(paymentMethodGroupId)
            val groupSnapshot = get(groupRef)
            if (groupSnapshot.exists) {
                val current = groupSnapshot.data<PaymentMethodGroupEntity>()
                set(groupRef, current.copy(isDeleted = true, updatedAt = now), encodeDefaults = true)
            }
            
            // 2. Clear groupId and groupLabel from associated payment methods
            methodsSnapshot.documents.forEach { doc ->
                val method = doc.data<PaymentMethodEntity>()
                set(doc.reference, method.copy(groupId = null, groupLabel = null, updatedAt = now), encodeDefaults = true)
            }
        }
    }

    override fun getPaymentMethodGroups(): Flow<List<PaymentMethodGroupEntity>> =
        getGroupCollection()
            .where { "isDeleted" equalTo false }
            .snapshots
            .map { snapshot -> snapshot.documents.map { it.data<PaymentMethodGroupEntity>() } }
            .flowOn(Dispatchers.Default)

    // ✅ 복원용 추가 구현
    override suspend fun getAllPaymentMethodsList(): List<PaymentMethodEntity> = withContext(Dispatchers.Default) {
        getPaymentMethodCollection().where { "isDeleted" equalTo false }.get().documents.map { it.data() }
    }

    override suspend fun getAllPaymentMethodGroupsList(): List<PaymentMethodGroupEntity> = withContext(Dispatchers.Default) {
        getGroupCollection().where { "isDeleted" equalTo false }.get().documents.map { it.data() }
    }

    override suspend fun insertPaymentMethods(methods: List<PaymentMethodEntity>) {
        methods.forEach { getPaymentMethodCollection().document(it.id).set(it, encodeDefaults = true) }
    }

    override suspend fun insertPaymentMethodGroups(groups: List<PaymentMethodGroupEntity>) {
        groups.forEach { getGroupCollection().document(it.id).set(it, encodeDefaults = true) }
    }

    override suspend fun deleteAllPaymentMethods() {
        getPaymentMethodCollection().get().documents.forEach { it.reference.delete() }
    }

    override suspend fun deleteAllPaymentMethodGroups() {
        getGroupCollection().get().documents.forEach { it.reference.delete() }
    }

    override suspend fun syncRemoteToLocal() {
        // Delegate에서 처리
    }

    override suspend fun syncLocalToRemote() {
        // Delegate에서 처리
    }
}
