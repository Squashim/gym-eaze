package com.example.gymeaze.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//Dark
val PrimaryGreen = Color(0xFF8BC34A)
val PrimaryGreenDark = Color(0xFF689F38)
val BackgroundDark = Color(0xFF1C1C1C)
val SurfaceDark = Color(0xFF2D2D2D)
val SurfaceVariant = Color(0xFF3D3D3D)
val OnSurfaceDark = Color(0xFFFFFFFF)
val OnSurfaceSecondary = Color(0xFFB0B0B0)

//Gradient
val Start = Color(0xFF3D3D3D)
val Middle = Color(0xFF4D4D4D)
val End = Color(0xFF8BC34A)

val ButtonOutlined = Color(0xFF4D4D4D)
val GoogleButton = Color(0xFFFFFFFF)
val GoogleButtonText = Color(0xFF1F1F1F)

val InputBackground = Color(0xFF3D3D3D)
val InputBorder = Color(0xFF5D5D5D)

val NavBarBackground = Color(0xFF1C1C1C)
val NavBarSelected = Color(0xFF8BC34A)
val NavBarUnselected = Color(0xFFB0B0B0)

//Light
val BackgroundLight = Color(0xFFFEFEFE)
val SurfaceLight = Color(0xFFF5F5F5)
val SurfaceVariantLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF141414)
val OnSurfaceLightSecondary = Color(0xFF737373)

val PrimaryGreenLight = Color(0xFF5D761E)

val PrimaryGreenDarkLight = Color(0xFF003900)
val PrimaryGreenSecondaryLight = Color(0xFFFFFFFF)

val InputBackgroundLight = Color(0xFFE5E5E5)
val InputBorderLight = Color(0xFFA1A1A1)
val InputErrorLight = Color(0xFFA7100B)

val NavBarBackgroundLight = Color(0xFFFFFFFF)
val NavBarSelectedLight = Color(0xFF5D761E)
val NavBarUnselectedLight = Color(0xFF737373)

val ButtonOutlinedLight = Color(0xFFE5E5E5)
val GoogleButtonLight = Color(0xFFFFFFFF)
val GoogleButtonTextLight = Color(0xFF1F1F1F)

//Gradient
val StartLight = Color(0xFFF5F5F5)
val MiddleLight = Color(0xFFE5E5E5)
val EndLight = Color(0xFF5D761E)



@Composable
fun getOnPrimaryColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) OnSurfaceDark else Color.White
}

@Composable
fun getBackgroundColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) BackgroundDark else BackgroundLight
}

@Composable
fun getSurfaceColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) SurfaceDark else SurfaceLight
}

@Composable
fun getSurfaceVariantColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) SurfaceVariant else SurfaceVariantLight
}


@Composable
fun getOnSurfaceColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) OnSurfaceDark else OnSurfaceLight
}

@Composable
fun getOnSurfaceSecondaryColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) OnSurfaceSecondary else OnSurfaceLightSecondary
}


@Composable
fun getPrimaryColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) PrimaryGreen else PrimaryGreenLight
}

@Composable
fun getPrimaryDarkColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) PrimaryGreenDark else PrimaryGreenDarkLight
}


@Composable
fun getNavBarBackgroundColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) NavBarBackground else NavBarBackgroundLight
}

@Composable
fun getNavBarSelectedColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) NavBarSelected else NavBarSelectedLight
}

@Composable
fun getNavBarUnselectedColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) NavBarUnselected else NavBarUnselectedLight
}

@Composable
fun getButtonOutlinedColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) ButtonOutlined else ButtonOutlinedLight
}

@Composable
fun getGoogleButtonColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) GoogleButton else GoogleButtonLight
}

@Composable
fun getGoogleButtonTextColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) GoogleButtonText else GoogleButtonTextLight
}


@Composable
fun getInputBackgroundColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) InputBackground else InputBackgroundLight
}

@Composable
fun getInputBorderColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) InputBorder else InputBorderLight
}

@Composable
fun getGradientStartColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) Start else StartLight
}

@Composable
fun getGradientMiddleColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) Middle else MiddleLight
}

@Composable
fun getGradientEndColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) End else EndLight
}

@Composable
fun getErrorColor() = InputErrorLight


@Composable
fun getCardBackgroundColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)
}

@Composable
fun getContinueButtonColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) Color(0xFF4CAF50) else PrimaryGreenLight
}