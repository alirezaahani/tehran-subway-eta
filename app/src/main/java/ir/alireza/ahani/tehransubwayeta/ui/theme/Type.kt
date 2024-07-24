package ir.alireza.ahani.tehransubwayeta.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ir.alireza.ahani.tehransubwayeta.R

object AppFont {
    var Samim = FontFamily(
        Font(R.font.samim),
        Font(R.font.samimb, FontWeight.Bold)
    )
}

private val defaultTypography = Typography();
val Typography = Typography(

    displayLarge = defaultTypography.displayLarge.copy(fontFamily = AppFont.Samim),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = AppFont.Samim),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = AppFont.Samim),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = AppFont.Samim),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = AppFont.Samim),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = AppFont.Samim),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = AppFont.Samim),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = AppFont.Samim),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = AppFont.Samim),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = AppFont.Samim),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = AppFont.Samim),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = AppFont.Samim),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = AppFont.Samim),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = AppFont.Samim),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = AppFont.Samim)
)