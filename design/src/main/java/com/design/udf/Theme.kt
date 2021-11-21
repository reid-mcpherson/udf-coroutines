package com.design.udf

import androidx.compose.runtime.*
import com.design.udf.Theme.LocalColors
import com.design.udf.Theme.isDarkTheme

public object Theme {
    public var isDarkTheme: MutableState<Boolean> = mutableStateOf(false)

    internal val LocalColors = compositionLocalOf<Colors> { LightColorPalette }

    public val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current
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
        content = content
    )
}


