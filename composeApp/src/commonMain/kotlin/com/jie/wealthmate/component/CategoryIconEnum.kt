package com.jie.wealthmate.component

import org.jetbrains.compose.resources.DrawableResource
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.emoji_u1f303
import wealthmate.composeapp.generated.resources.emoji_u1f3d9
import wealthmate.composeapp.generated.resources.emoji_u1f3db
import wealthmate.composeapp.generated.resources.emoji_u1f3e0
import wealthmate.composeapp.generated.resources.emoji_u1f3e2
import wealthmate.composeapp.generated.resources.emoji_u1f3e3
import wealthmate.composeapp.generated.resources.emoji_u1f3e5
import wealthmate.composeapp.generated.resources.emoji_u1f3e6
import wealthmate.composeapp.generated.resources.emoji_u1f3e7
import wealthmate.composeapp.generated.resources.emoji_u1f3ea
import wealthmate.composeapp.generated.resources.emoji_u1f3ec
import wealthmate.composeapp.generated.resources.emoji_u1f3ed
import wealthmate.composeapp.generated.resources.emoji_u1f4b0
import wealthmate.composeapp.generated.resources.emoji_u1f4b3
import wealthmate.composeapp.generated.resources.emoji_u1f4b5
import wealthmate.composeapp.generated.resources.emoji_u1f4b8
import wealthmate.composeapp.generated.resources.emoji_u1f4bc
import wealthmate.composeapp.generated.resources.emoji_u1f4c8
import wealthmate.composeapp.generated.resources.emoji_u1f4c9
import wealthmate.composeapp.generated.resources.emoji_u1f4ca


enum class CategoryIconEnum(val resource: DrawableResource) {
    CATEGORY_EMOJI_U1F303(Res.drawable.emoji_u1f303),
    CATEGORY_EMOJI_U1F3D9(Res.drawable.emoji_u1f3d9),
    CATEGORY_EMOJI_U1F3DB(Res.drawable.emoji_u1f3db),
    CATEGORY_EMOJI_U1F3E0(Res.drawable.emoji_u1f3e0),
    CATEGORY_EMOJI_U1F3E2(Res.drawable.emoji_u1f3e2),
    CATEGORY_EMOJI_U1F3E3(Res.drawable.emoji_u1f3e3),
    CATEGORY_EMOJI_U1F3E5(Res.drawable.emoji_u1f3e5),
    CATEGORY_EMOJI_U1F3E6(Res.drawable.emoji_u1f3e6),
    CATEGORY_EMOJI_U1F3E7(Res.drawable.emoji_u1f3e7),
    CATEGORY_EMOJI_U1F3EA(Res.drawable.emoji_u1f3ea),
    CATEGORY_EMOJI_U1F3EC(Res.drawable.emoji_u1f3ec),
    CATEGORY_EMOJI_U1F3ED(Res.drawable.emoji_u1f3ed),
    CATEGORY_EMOJI_U1F4B0(Res.drawable.emoji_u1f4b0),
    CATEGORY_EMOJI_U1F4B3(Res.drawable.emoji_u1f4b3),
    CATEGORY_EMOJI_U1F4B5(Res.drawable.emoji_u1f4b5),
    CATEGORY_EMOJI_U1F4B8(Res.drawable.emoji_u1f4b8),
    CATEGORY_EMOJI_U1F4BC(Res.drawable.emoji_u1f4bc),
    CATEGORY_EMOJI_U1F4C8(Res.drawable.emoji_u1f4c8),
    CATEGORY_EMOJI_U1F4C9(Res.drawable.emoji_u1f4c9),
    CATEGORY_EMOJI_U1F4CA(Res.drawable.emoji_u1f4ca),
    ;

    companion object {
        val defaultCategoryIcon = CATEGORY_EMOJI_U1F4B0

        fun creator(name: String): CategoryIconEnum =
            CategoryIconEnum.entries.find { it.name == name } ?: defaultCategoryIcon

        val incomeCategoryIcon: List<CategoryIconEnum>
            get() {
                return listOf(
                    CATEGORY_EMOJI_U1F303,
                    CATEGORY_EMOJI_U1F3D9,
                    CATEGORY_EMOJI_U1F3DB,
                    CATEGORY_EMOJI_U1F3E0,
                    CATEGORY_EMOJI_U1F3E2,
                    CATEGORY_EMOJI_U1F3E3,
                    CATEGORY_EMOJI_U1F3E5,
                    CATEGORY_EMOJI_U1F3E6,
                    CATEGORY_EMOJI_U1F3E7,
                    CATEGORY_EMOJI_U1F3EA,
                    CATEGORY_EMOJI_U1F3EC,
                    CATEGORY_EMOJI_U1F3ED,
                    CATEGORY_EMOJI_U1F4B0,
                    CATEGORY_EMOJI_U1F4B3,
                    CATEGORY_EMOJI_U1F4B5,
                    CATEGORY_EMOJI_U1F4B8,
                    CATEGORY_EMOJI_U1F4BC,
                    CATEGORY_EMOJI_U1F4C8,
                    CATEGORY_EMOJI_U1F4C9,
                    CATEGORY_EMOJI_U1F4CA,
                )
            }
    }
}


