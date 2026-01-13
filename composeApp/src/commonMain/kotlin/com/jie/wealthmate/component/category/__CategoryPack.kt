import androidx.compose.ui.graphics.vector.ImageVector
import com.jie.wealthmate.component.category.categoryIncome.EmojiU1f303
import com.jie.wealthmate.component.category.categoryIncome.EmojiU1f3d9
import com.jie.wealthmate.component.category.categoryIncome.EmojiU1f3db
import com.jie.wealthmate.component.category.categoryIncome.EmojiU1f3e0
import com.jie.wealthmate.component.category.categoryIncome.EmojiU1f3e2
import com.jie.wealthmate.component.category.categoryIncome.EmojiU1f4b3
import com.jie.wealthmate.component.category.categoryIncome.EmojiU1f4b8
import com.jie.wealthmate.component.category.categoryIncome.EmojiU1f4c8
import com.jie.wealthmate.component.category.categoryIncome.EmojiU1f4c9
import com.jie.wealthmate.component.category.categoryIncome.EmojiU1f4ca
import kotlin.collections.List as ____KtList

object CategoryIncomePack

private var __AllIcons: ____KtList<ImageVector>? = null

val CategoryIncomePack.AllIcons: ____KtList<ImageVector>
    get() {
        if (__AllIcons != null) {
            return __AllIcons!!
        }
        __AllIcons = listOf(
            EmojiU1f3d9,
            EmojiU1f3db,
            EmojiU1f3e0,
            EmojiU1f3e2,
            EmojiU1f4b3,
            EmojiU1f4b8,
            EmojiU1f4c8,
            EmojiU1f4c9,
            EmojiU1f4ca,
            EmojiU1f303,
        )
        return __AllIcons!!
    }

fun getCategoryIncome(index: Int): ImageVector {
    return CategoryIncomePack.AllIcons[index]
}
