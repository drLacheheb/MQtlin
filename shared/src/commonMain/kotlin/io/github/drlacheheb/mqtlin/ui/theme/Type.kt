package io.github.drlacheheb.mqtlin.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val HeadlineSm = TextStyle(
    fontFamily = FontFamily.Default,
    fontSize = 20.sp,
    lineHeight = 28.sp,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = (-0.02).sp,
    color = DarkOnSurface
)

val UiLabelBold = TextStyle(
    fontFamily = FontFamily.Default,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    fontWeight = FontWeight.SemiBold,
    color = DarkOnSurface
)

val UiLabelReg = TextStyle(
    fontFamily = FontFamily.Default,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    fontWeight = FontWeight.Normal,
    color = DarkOnSurface
)

val MonoCode = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontSize = 13.sp,
    lineHeight = 20.sp,
    fontWeight = FontWeight.Normal,
    color = DarkOnSurface
)

val MonoTopic = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    fontWeight = FontWeight.Medium,
    letterSpacing = 0.02.sp,
    color = DarkOnSurfaceVariant
)

val LabelXs = TextStyle(
    fontFamily = FontFamily.Default,
    fontSize = 11.sp,
    lineHeight = 12.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 0.05.sp,
    color = DarkOnSurfaceVariant
)

val BodyCompact = TextStyle(
    fontFamily = FontFamily.Default,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    fontWeight = FontWeight.Normal,
    color = DarkOnSurface
)

val MqtlinTypography = Typography(
    headlineSmall = HeadlineSm,
    titleMedium = UiLabelBold,
    bodyMedium = BodyCompact,
    bodySmall = MonoCode,
    labelSmall = LabelXs
)

