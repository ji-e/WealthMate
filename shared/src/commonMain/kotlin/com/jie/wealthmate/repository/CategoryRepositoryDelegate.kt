package com.jie.wealthmate.repository

import com.jie.wealthmate.account.AccountAwareDelegate
import com.jie.wealthmate.account.AccountProvider
import com.jie.wealthmate.database.eneity.CategoryEntity

class CategoryRepositoryDelegate(
    accountProvider: AccountProvider,
    private val authRepository: AuthRepository,
    private val localRepository: CategoryRepository,
    private val firestoreRepository: CategoryRepository,
) : CategoryRepository {
    private val d = AccountAwareDelegate(
        special = firestoreRepository,
        normal = localRepository,
        accountProvider = accountProvider,
    )

    override suspend fun insertCategory(
        icon: String, largeCategory: String, middleLabel: String,
        sort: Long, isFixed: Boolean, tagLabels: List<String>,
    ) = d.dualCall { it.insertCategory(icon, largeCategory, middleLabel, sort, isFixed, tagLabels) }

    override suspend fun updateCategory(category: CategoryEntity) =
        d.dualCall { it.updateCategory(category) }

    override suspend fun updateCategoriesSort(updates: List<Pair<String, Long>>) =
        d.dualCall { it.updateCategoriesSort(updates) }

    override suspend fun deleteCategory(categoryId: String) =
        d.dualCall { it.deleteCategory(categoryId) }

    override suspend fun getCategoryById(categoryId: String) =
        d.call { it.getCategoryById(categoryId) }

    override fun getCategoryByIdFlow(categoryId: String) =
        d.flatFlow { it.getCategoryByIdFlow(categoryId) }

    override fun getAllCategories() =
        d.flatFlow { it.getAllCategories() }

    override fun getCategoriesByLargeCategory(largeCategory: String) =
        d.flatFlow { it.getCategoriesByLargeCategory(largeCategory) }

    // ✅ 복원용 메서드들
    override suspend fun getAllCategoriesList(): List<CategoryEntity> = d.call { it.getAllCategoriesList() }
    override suspend fun insertCategories(categories: List<CategoryEntity>) = d.dualCall { it.insertCategories(categories) }
    override suspend fun deleteAllCategories() = d.dualCall { it.deleteAllCategories() }

    // ✅ 핵심 복원 로직: Firestore -> Local
    override suspend fun syncRemoteToLocal() {
        d.restore { special, normal ->
            val remoteData = special.getAllCategoriesList()
            if (remoteData.isNotEmpty()) {
                normal.deleteAllCategories()
                normal.insertCategories(remoteData)
            }
        }
    }
}
