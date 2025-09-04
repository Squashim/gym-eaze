package com.example.gymeaze.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryLight = Color(0xFF94C521)
val PrimaryDark = Color(0xFF689F38)

val OnPrimaryLight = Color(0xFFFFFFFF)
val OnPrimaryDark = Color(0xff141414)


val SecondaryLight = Color(0xFF141414)
val SecondaryDark = Color(0xFFe7e4fd)

val OnSecondaryLight = Color(0xFFFFFFFF)
val OnSecondaryDark = Color(0xff141414)

val BackgroundLight = Color(0xFFFEFEFE)
val BackgroundDark = Color(0xFF1C1C1C)

val SurfaceLight = Color(0xFFF5F5F5)
val SurfaceDark = Color(0xFF2D2D2D)

val SurfaceVariantLight = Color(0xFFFFFFFF)
val SurfaceVariantDark = Color(0xFF3D3D3D)

val OnSurfaceLight = Color(0xFF141414)
val OnSurfaceDark = Color(0xFFFFFFFF)

val OnSurfaceSecondaryLight = Color(0xFF737373)
val OnSurfaceSecondaryDark = Color(0xFFB0B0B0)

val InputErrorLight = Color(0xFFA71D0B)
val InputErrorDark = Color(0xFFE25F50)

val ButtonOutlinedLight = Color(0xFFE5E5E5)
val ButtonOutlinedDark = Color(0xFF4D4D4D)

val GoogleButtonLight = Color(0xFFFFFFFF)
val GoogleButtonDark = Color(0xFFFFFFFF)

val GoogleButtonTextLight = Color(0xFF1F1F1F)
val GoogleButtonTextDark = Color(0xFF1F1F1F)

val InputBackgroundLight = Color(0xFFE5E5E5)
val InputBackgroundDark = Color(0xFF3D3D3D)

val InputBorderLight = Color(0xFFA1A1A1)
val InputBorderDark = Color(0xFF5D5D5D)

val NavBarBackgroundLight = Color(0xFFFFFFFF)
val NavbarBackgroundDark = Color(0xFF1C1C1C)

val NavBarSelectedLight = Color(0xFF5D761E)
val NavBarSelectedDark = Color(0xFF8BC34A)

val NavBarUnselectedLight = Color(0xFF737373)
val NavBarUnselectedDark = Color(0xFFB0B0B0)

//Gradient
val MiddleLight = Color(0xFFE5E5E5)
val MiddleDark = Color(0xFF4D4D4D)

val YellowLight = Color(0x1aeab308)
val YellowDark = Color(0x1aca8a04)

val OnYellowLight = Color(0xff854d0e)
val OnYellowDark = Color(0xffeab308)



@Composable
fun getPrimaryColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) PrimaryDark else PrimaryLight
}

@Composable
fun getOnPrimaryColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) OnPrimaryDark else OnPrimaryLight
}

@Composable
fun getSecondaryColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) SecondaryDark else SecondaryLight
}

@Composable
fun getOnSecondaryColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) OnSecondaryDark else OnSecondaryLight
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
    return if (isDarkTheme) SurfaceVariantDark else SurfaceVariantLight
}


@Composable
fun getOnSurfaceColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) OnSurfaceDark else OnSurfaceLight
}

@Composable
fun getOnSurfaceSecondaryColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) OnSurfaceSecondaryDark else OnSurfaceSecondaryLight
}

@Composable
fun getNavBarBackgroundColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) NavbarBackgroundDark else NavBarBackgroundLight
}

@Composable
fun getNavBarSelectedColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) NavBarSelectedDark else NavBarSelectedLight
}

@Composable
fun getNavBarUnselectedColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) NavBarUnselectedDark else NavBarUnselectedLight
}

@Composable
fun getButtonOutlinedColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) ButtonOutlinedDark else ButtonOutlinedLight
}

@Composable
fun getGoogleButtonColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) GoogleButtonDark else GoogleButtonLight
}

@Composable
fun getGoogleButtonTextColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) GoogleButtonTextDark else GoogleButtonTextLight
}


@Composable
fun getInputBackgroundColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) InputBackgroundDark else InputBackgroundLight
}

@Composable
fun getInputBorderColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) InputBorderDark else InputBorderLight
}

@Composable
fun getGradientMiddleColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) MiddleDark else MiddleLight
}

@Composable
fun getErrorColor(): Color{
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) InputErrorDark else InputErrorLight
}

@Composable
fun getYellowColor(): Color{
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) YellowDark else YellowLight
}

@Composable
fun getOnYellowColor(): Color{
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) OnYellowDark else OnYellowLight
}


@Composable
fun getCardBackgroundColor(): Color {
    val isDarkTheme = LocalDarkTheme.current
    return if (isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)
}