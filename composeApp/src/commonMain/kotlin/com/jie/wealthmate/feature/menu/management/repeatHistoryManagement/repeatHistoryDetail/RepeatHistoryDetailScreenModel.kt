package com.jie.wealthmate.feature.menu.management.repeatHistoryManagement.repeatHistoryDetail

import androidx.compose.ui.text.input.TextFieldValue
import com.jie.wealthmate.base.BaseScreenModel
import com.jie.wealthmate.database.eneity.RepeatCycleEntity
import com.jie.wealthmate.feature.calendar.addHistory.component.RepeatCycleEnum
import com.jie.wealthmate.feature.menu.management.categoryManagement.component.LargeCategoryEnum
import com.jie.wealthmate.repository.CategoryRepository
import com.jie.wealthmate.repository.PaymentMethodRepository
import com.jie.wealthmate.repository.RepeatCycleRepository
import com.jie.wealthmate.utils.default
import com.jie.wealthmate.utils.formatRemoveCommas
import com.jie.wealthmate.utils.toEpochMilliseconds
import com.jie.wealthmate.utils.toLocalDate
import com.jie.wealthmate.utils.today
import com.jie.wealthmate.vo.CategoryTagVo
import com.jie.wealthmate.vo.CategoryTagVo.Companion.mapperToVo
import com.jie.wealthmate.vo.CategoryVo
import com.jie.wealthmate.vo.CategoryVo.Companion.mapperToVo
import com.jie.wealthmate.vo.PaymentMethodVo
import com.jie.wealthmate.vo.PaymentMethodVo.Companion.mapperToVo
import kotlinx.datetime.LocalDate

class RepeatHistoryDetailScreenModel(
    private val repeatCycleRepository: RepeatCycleRepository,
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
) : BaseScreenModel<RepeatHistoryDetailUiState>() {

    var repeatCycleId: String? = null

    override val initialState: RepeatHistoryDetailUiState
        get() = RepeatHistoryDetailUiState()

    init {
        getCategories(LargeCategoryEnum.EXPENSES)
        getPaymentMethods()
    }

    fun updateInit(repeatCycleId: String) {
        this.repeatCycleId = repeatCycleId
        getRepeatCycle()
    }

    fun updateLargeCategory(largeCategory: LargeCategoryEnum) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                selectedLargeCategory = largeCategory
            )
        }
        getCategories(largeCategory)
    }


    fun updateDate(type: String, date: LocalDate?) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                startDate = if (type == START_DATE) date ?: today else state.startDate,
                endDate = if (type == END_DATE) date else state.endDate
            )
        }
    }

    fun updateRepeatCycle(repeatCycle: RepeatCycleEnum?) {
        repeatCycle ?: return

        reduceState { state ->
            state.copy(
                isDataChanged = true,
                repeatCycle = repeatCycle,
                repeatCycleDate = null,
                repeatCycleDateFull = null
            )
        }
    }

    fun updateRepeatCycleDate(repeatCycleDate: Int?) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                repeatCycleDate = repeatCycleDate
            )
        }
    }

    fun updateRepeatCycleDateFull(month: Int, day: Int) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                // 2월 29일을 처리하기 위해 항상 윤년인 2000년을 기준으로 저장합니다.
                repeatCycleDateFull = LocalDate(2000, month, day)
            )
        }
    }

    fun updateAmount(amount: TextFieldValue) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                amount = amount
            )
        }
    }

    fun updateCategory(category: CategoryVo?) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                category = category
            )
        }
    }

    fun updateCategoryTag(categoryTag: CategoryTagVo?) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                categoryTag = categoryTag
            )
        }
    }

    fun updatePaymentMethod(paymentMethod: PaymentMethodVo?) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                paymentMethod = paymentMethod
            )
        }
    }

    fun updateContent(content: TextFieldValue) {
        reduceState { state ->
            state.copy(
                isDataChanged = true,
                content = content
            )
        }
    }

    private fun getRepeatCycle() {
        launchSafe(
            block = {
                repeatCycleRepository.getRepeatCycleById(repeatCycleId.default())
            }
        ) { response ->
            val repeatCycle = response?.repeatCycle ?: return@launchSafe
            val repeatCycleEnum = RepeatCycleEnum.create(repeatCycle.repeatCycle)

            reduceState { state ->
                state.copy(
                    selectedLargeCategory = LargeCategoryEnum.creator(repeatCycle.largeCategory),
                    startDate = repeatCycle.startDate.toLocalDate(),
                    endDate = repeatCycle.endDate?.toLocalDate(),
                    repeatCycle = repeatCycleEnum,
                    repeatCycleDate = if (repeatCycleEnum == RepeatCycleEnum.WEEKLY) repeatCycle.dayOfWeek else if (repeatCycleEnum == RepeatCycleEnum.MONTHLY) repeatCycle.dayOfMonth else null,
                    repeatCycleDateFull = repeatCycle.date.toLocalDate(),
                    content = TextFieldValue(repeatCycle.content.default()),
                    amount = TextFieldValue(repeatCycle.amount.default().toString()),
                    category = response.category?.mapperToVo(),
                    categoryTag = response.categoryTag?.mapperToVo(),
                    paymentMethod = response.paymentMethod?.mapperToVo(),
                )
            }

        }
    }

    private fun getCategories(largeCategoryEnum: LargeCategoryEnum) {
        categoryRepository.getCategoriesByLargeCategory(largeCategoryEnum.name)
            .apiFlow { response ->
                reduceState { state ->
                    state.copy(
                        categoryItems = response.map { it.mapperToVo() }
                    )
                }
            }
    }

    private fun getPaymentMethods() {
        paymentMethodRepository.getPaymentMethods()
            .apiFlow { response ->
                println("response: $response")
                reduceState { state ->
                    state.copy(
                        paymentMethodItems = response.map {
                            PaymentMethodVo(
                                id = it.paymentMethod.id,
                                label = it.paymentMethod.label,
                                groupId = it.group?.id,
                                groupLabel = it.group?.label,
                                sort = it.paymentMethod.sort
                            )
                        }
                    )
                }
            }
    }

    fun modifyRepeatCycle() {
        val repeatCycleId = repeatCycleId ?: return

        val uiState = container.uiState.value
        launchSafe(
            block = {
                repeatCycleRepository.updateRepeatCycle(
                    RepeatCycleEntity(
                        id = repeatCycleId,
                        largeCategory = uiState.selectedLargeCategory.name,
                        content = uiState.content.text,
                        amount = uiState.amount.text.formatRemoveCommas().toLong(),
                        repeatCycle = uiState.repeatCycle.name,
                        dayOfWeek = if (uiState.repeatCycle == RepeatCycleEnum.WEEKLY) uiState.repeatCycleDate else null,
                        dayOfMonth = if (uiState.repeatCycle == RepeatCycleEnum.MONTHLY) uiState.repeatCycleDate else null,
                        date = uiState.repeatCycleDateFull?.toEpochMilliseconds().default(),
                        startDate = uiState.startDate.toEpochMilliseconds(),
                        endDate = uiState.endDate?.toEpochMilliseconds(),
                        categoryId = uiState.category?.id,
                        categoryTagId = uiState.categoryTag?.id,
                        paymentMethodId = uiState.paymentMethod?.id,
                    )
                )
            }
        ) {
            showSnackbar("반복 정보가 수정 되었습니다.")
            reduceState { state ->
                state.copy(
                    isDataChanged = false
                )
            }
        }
    }

    companion object {
        const val START_DATE = "시작일"
        const val END_DATE = "종료일"
    }
}
