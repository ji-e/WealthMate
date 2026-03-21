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
    ) = d.current.insertCategory(icon, largeCategory, middleLabel, sort, isFixed, tagLabels)

    override suspend fun updateCategory(category: CategoryEntity) =
        d.current.updateCategory(category)

    override suspend fun updateCategoriesSort(updates: List<Pair<String, Long>>) =
        d.current.updateCategoriesSort(updates)

    override suspend fun deleteCategory(categoryId: String) =
        d.current.deleteCategory(categoryId)

    override suspend fun getCategoryById(categoryId: String) =
        d.current.getCategoryById(categoryId)

    override fun getCategoryByIdFlow(categoryId: String) =
        d.flatFlow { it.getCategoryByIdFlow(categoryId) }

    override fun getAllCategories() =
        d.flatFlow { it.getAllCategories() }

    override fun getCategoriesByLargeCategory(largeCategory: String) =
        d.flatFlow { it.getCategoriesByLargeCategory(largeCategory) }

}
