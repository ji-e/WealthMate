package com.jie.wealthmate.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import wealthmate.composeapp.generated.resources.Res
import wealthmate.composeapp.generated.resources.wanted_sans_bold
import wealthmate.composeapp.generated.resources.wanted_sans_medium
import wealthmate.composeapp.generated.resources.wanted_sans_regular
import wealthmate.composeapp.generated.resources.wanted_sans_semi_bold


@Composable
fun wantedSansFontFamily() = FontFamily(
    Font(resource = Res.font.wanted_sans_bold, weight = FontWeight.Bold),
    Font(resource = Res.font.wanted_sans_semi_bold, weight = FontWeight.SemiBold),
    Font(resource = Res.font.wanted_sans_medium, weight = FontWeight.Medium),
    Font(resource = Res.font.wanted_sans_regular, weight = FontWeight.Normal)
)