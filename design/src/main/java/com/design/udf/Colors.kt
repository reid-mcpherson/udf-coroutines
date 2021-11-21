package com.design.udf

import androidx.compose.ui.graphics.Color

interface Colors {
    val keyColor: Color
    val keyColorInvert: Color
    val keyColorPressed: Color
    val blueSustainer: Color
    val greenStreaming: Color
    val greenSustainer: Color
    val redContribute: Color
    val redUpdate: Color
    val white: Color
    val black: Color
    val gray1: Color
    val gray2: Color
    val gray3: Color
    val gray4: Color
    val gray5: Color
    val gray6: Color
    val gray7: Color
    val gray8: Color
    val gray9: Color
    val imageGalleryOverlay: Color
    val inTheNewsBg: Color
    val divider: Color
    val dividerDark: Color
    val tabBar: Color
    val pressedState: Color
    val pressedStateLight: Color
    val androidKnobs: Color
}

internal object LightColorPalette : Colors {
    override val keyColor = Color(0xFF00749B)
    override val keyColorInvert = Color(0xFF4DAAE0)
    override val keyColorPressed = Color(0xFF006181)
    override val blueSustainer = Color(0xFF00749B)
    override val greenStreaming = Color(0xFF687000)
    override val greenSustainer = Color(0xFFC4CF22)
    override val redContribute = Color(0xFFCD3227)
    override val redUpdate = Color(0xFFB01417)
    override val white = Color(0xFFFFFFFF)
    override val black = Color(0xFF000000)
    override val gray1 = Color(0xFFFFFFFF)
    override val gray2 = Color(0xFFF9F9F9)
    override val gray3 = Color(0xFFEFF2F3)
    override val gray4 = Color(0xFFE8EBEC)
    override val gray5 = Color(0xFFCBD0D2)
    override val gray6 = Color(0xFF757575)
    override val gray7 = Color(0xFF363636)
    override val gray8 = Color(0xFF363636)
    override val gray9 = Color(0xFF000000)
    override val imageGalleryOverlay: Color = Color(0xA62C2C2C)
    override val inTheNewsBg: Color = Color(0xFF161616)
    override val divider: Color = Color(0xFFEFF2F3)
    override val dividerDark: Color = Color(0xFFE8EBEC)
    override val tabBar: Color = Color(0xFFF9F9F9)
    override val pressedState: Color = Color(0x14000000)
    override val pressedStateLight: Color = Color(0x3DFFFFFF)
    override val androidKnobs: Color = Color(0xFFFFFFFF)
}

internal object DarkColorPalette : Colors {
    override val keyColor = Color(0xFF4DAAE0)
    override val keyColorInvert = Color(0xFF00749B)
    override val keyColorPressed = Color(0xFF3CB7FF)
    override val blueSustainer = Color(0xFF00749B)
    override val greenStreaming = Color(0xFF7F8906)
    override val greenSustainer = Color(0xFFC4CF22)
    override val redContribute = Color(0xFFCD3227)
    override val redUpdate = Color(0xFFE94346)
    override val white = Color(0xFFFFFFFF)
    override val black = Color(0xFF000000)
    override val gray1 = Color(0xFF000000)
    override val gray2 = Color(0xFF191919)
    override val gray3 = Color(0xFF1C1C1E)
    override val gray4 = Color(0xFF222324)
    override val gray5 = Color(0xFF363636)
    override val gray6 = Color(0xFF757575)
    override val gray7 = Color(0xFFCBD0D2)
    override val gray8 = Color(0xFF363636)
    override val gray9 = Color(0xFFFFFFFF)
    override val imageGalleryOverlay: Color = Color(0xA62C2C2C)
    override val inTheNewsBg: Color = Color(0xFF161616)
    override val divider: Color = Color(0xFF1C1C1E)
    override val dividerDark: Color = Color(0xFF222324)
    override val tabBar: Color = Color(0xFF1B1B1B)
    override val pressedState: Color = Color(0x3DFFFFFF)
    override val pressedStateLight: Color = Color(0x14000000)
    override val androidKnobs: Color = Color(0xFFA5A5A5)
}

