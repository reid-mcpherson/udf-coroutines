package com.design.udf

import androidx.compose.runtime.*
import com.design.udf.Theme.LocalColors
import com.design.udf.Theme.LocalTypography
import com.design.udf.Theme.isDarkTheme

public object Theme {
    public var isDarkTheme: MutableState<Boolean> = mutableStateOf(false)

    internal val LocalColors = compositionLocalOf<Colors> { LightColorPalette }

    internal val LocalTypography = staticCompositionLocalOf<Typography> {
        TypographyImpl
    }

    public val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    public val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}

@Composable
public fun Theme(
    content: @Composable () -> Unit
) {
    val colors = if (isDarkTheme.value) DarkColorPalette else LightColorPalette

    ProvideTheme(colors) {
        content()
    }
}

@Composable
internal fun ProvideTheme(
    colors: Colors,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalColors provides colors,
        LocalTypography provides TypographyImpl,
        content = content
    )
}


