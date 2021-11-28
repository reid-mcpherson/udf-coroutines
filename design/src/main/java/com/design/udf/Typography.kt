package com.design.udf

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

internal val CrimsonText = FontFamily(
    Font(R.font.crimsonpro_regular),
    Font(R.font.crimsonpro_bold, FontWeight.Bold),
    Font(R.font.crimsonpro_semibold, FontWeight.SemiBold),
    Font(R.font.crimsonpro_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.crimsonpro_italic, style = FontStyle.Italic)
)

internal val Roboto = FontFamily(
    Font(R.font.roboto_regular),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_bold, FontWeight.Bold),
    Font(R.font.roboto_black, FontWeight.Black),
    Font(R.font.roboto_italic, style = FontStyle.Italic)
)

public interface Typography {

    //region Primary Text Styles
    @Composable
    public fun author(): TextStyle

    @Composable
    public fun body(): TextStyle

    @Composable
    public fun bodyLargeSans(): TextStyle

    @Composable
    public fun caption(): TextStyle

    @Composable
    public fun contribute(): TextStyle

    @Composable
    public fun eyebrow(): TextStyle

    @Composable
    public fun h1(): TextStyle

    @Composable
    public fun h2(): TextStyle

    @Composable
    public fun h2Sans(): TextStyle

    @Composable
    public fun h3(): TextStyle

    @Composable
    public fun imageCredit(): TextStyle

    @Composable
    public fun inTheNews(): TextStyle

    @Composable
    public fun inlineAudioPlayerTime(): TextStyle

    @Composable
    public fun notification(): TextStyle

    @Composable
    public fun smallText(): TextStyle

    @Composable
    public fun streamingNow(): TextStyle

    @Composable
    public fun updated(): TextStyle
    //endregion

    //region Secondary Text Styles
    @Composable
    public fun authorLink(): TextStyle

    @Composable
    public fun authorLinkPressed(): TextStyle

    @Composable
    public fun bodyBold(): TextStyle

    @Composable
    public fun bodyBoldItalic(): TextStyle

    @Composable
    public fun bodyItalic(): TextStyle

    @Composable
    public fun bodyLink(): TextStyle

    @Composable
    public fun bodyLinkPressed(): TextStyle

    @Composable
    public fun bodyLinkBold(): TextStyle

    @Composable
    public fun bodyLinkBoldPressed(): TextStyle

    @Composable
    public fun bodyLinkBoldItalic(): TextStyle

    @Composable
    public fun bodyLinkBoldItalicPressed(): TextStyle

    @Composable
    public fun bodyLinkItalic(): TextStyle

    @Composable
    public fun bodyLinkItalicPressed(): TextStyle

    @Composable
    public fun bodyLargeSansCentered(): TextStyle

    @Composable
    public fun bodyLargeSansWhite(): TextStyle

    @Composable
    public fun bodySmall(): TextStyle

    @Composable
    public fun bodySmallCentered(): TextStyle

    @Composable
    public fun captionCentered(): TextStyle

    @Composable
    public fun captionGray6(): TextStyle

    @Composable
    public fun captionGray6Centered(): TextStyle

    @Composable
    public fun captionItalic(): TextStyle

    @Composable
    public fun captionLinkRight(): TextStyle

    @Composable
    public fun captionLinkRightPressed(): TextStyle

    @Composable
    public fun captionWhite(): TextStyle

    @Composable
    public fun captionWhiteItalic(): TextStyle

    @Composable
    public fun captionBold(): TextStyle

    @Composable
    public fun captionBoldGray6(): TextStyle

    @Composable
    public fun eyebrowBlack(): TextStyle

    //TODO: Heavy Roboto font?
    @Composable
    public fun eyebrowHeavyGray8(): TextStyle

    @Composable
    public fun eyebrowHeavyWhite(): TextStyle

    @Composable
    public fun eyebrowLargeCentered(): TextStyle

    @Composable
    public fun eyebrowLargeWhiteCentered(): TextStyle

    @Composable
    public fun eyebrowMedium(): TextStyle

    @Composable
    public fun eyebrowMediumCentered(): TextStyle

    @Composable
    public fun eyebrowMediumTitleCase(): TextStyle

    @Composable
    public fun eyebrowMediumWhiteCentered(): TextStyle

    @Composable
    public fun h2Centered(): TextStyle

    @Composable
    public fun h2SansWhite(): TextStyle

    @Composable
    public fun notificationAppName(): TextStyle

    @Composable
    public fun notificationCopy(): TextStyle

    @Composable
    public fun notificationHeadline(): TextStyle

    @Composable
    public fun notificationSubCopy(): TextStyle

    @Composable
    public fun notificationTitle(): TextStyle

    @Composable
    public fun smallTextCenteredSecondaryAction(): TextStyle

    @Composable
    public fun smallTextWhite(): TextStyle

    @Composable
    public fun smallTextBold(): TextStyle

    @Composable
    public fun smallTextBoldCentered(): TextStyle

    @Composable
    public fun smallTextBoldOnBoarding(): TextStyle

    @Composable
    public fun smallTextBoldWhite(): TextStyle

    @Composable
    public fun smallTextBoldWhiteCentered(): TextStyle

    @Composable
    public fun smallTextMedium(): TextStyle

    @Composable
    public fun smallTextMediumCentered(): TextStyle

    @Composable
    public fun smallTextMediumCenteredGray1(): TextStyle

    @Composable
    public fun smallTextMediumGray7(): TextStyle

    @Composable
    public fun smallTextMediumLink(): TextStyle

    @Composable
    public fun smallTextMediumLinkPressed(): TextStyle

    @Composable
    public fun smallTextMediumLinkRight(): TextStyle

    @Composable
    public fun smallTextMediumLinkRightPressed(): TextStyle

    @Composable
    public fun tabBarActive(): TextStyle

    @Composable
    public fun tabBarInactive(): TextStyle
    //endregion

}

internal object TypographyImpl : Typography {

    //region Primary Text Styles
    private val author = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Left
    )

    private val body = TextStyle(
        fontFamily = CrimsonText,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Left
    )

    private val bodyLargeSans = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Left
    )

    private val caption = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Left,
    )

    private val contribute = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Left
    )

    private val eyebrow = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.sp,
        textAlign = TextAlign.Left
    )
    private val h1: TextStyle = TextStyle(
        fontFamily = CrimsonText,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Left
    )
    private val h2: TextStyle = TextStyle(
        fontFamily = CrimsonText,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Left
    )

    private val h2Sans: TextStyle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Left
    )

    private val h3 = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Left
    )

    private val imageCredit = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        fontSize = 11.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Right
    )

    private val inTheNews = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Left
    )

    private val inlineAudioPlayerTime = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Left
    )

    private val notification = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Left
    )

    private val smallText = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Left
    )

    private val streamingNow = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Left
    )

    private val updated = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Left
    )

    private val tabBar: TextStyle = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Center
    )

    @Composable
    override fun author(): TextStyle = author.copy(color = Theme.colors.gray7)

    @Composable
    override fun body(): TextStyle = body.copy(color = Theme.colors.gray7)

    @Composable
    override fun bodyLargeSans(): TextStyle = bodyLargeSans.copy(color = Theme.colors.gray7)

    @Composable
    override fun caption(): TextStyle = caption.copy(color = Theme.colors.gray7)

    @Composable
    override fun contribute(): TextStyle = contribute.copy(color = Theme.colors.white)

    @Composable
    override fun eyebrow(): TextStyle = eyebrow.copy(color = Theme.colors.gray7)

    @Composable
    override fun h1(): TextStyle = h1.copy(color = Theme.colors.gray7)

    @Composable
    override fun h2(): TextStyle = h2.copy(color = Theme.colors.gray7)

    @Composable
    override fun h2Sans(): TextStyle = h2Sans.copy(color = Theme.colors.gray7)

    @Composable
    override fun h3(): TextStyle = h3.copy(color = Theme.colors.gray7)

    @Composable
    override fun imageCredit(): TextStyle = imageCredit.copy(color = Theme.colors.gray6)

    @Composable
    override fun inTheNews(): TextStyle = inTheNews.copy(color = Theme.colors.white)

    @Composable
    override fun inlineAudioPlayerTime(): TextStyle =
        inlineAudioPlayerTime.copy(color = Theme.colors.gray7)

    @Composable
    override fun notification(): TextStyle = notification.copy(color = Theme.colors.gray7)

    @Composable
    override fun smallText(): TextStyle = smallText.copy(color = Theme.colors.gray7)

    @Composable
    override fun streamingNow(): TextStyle = streamingNow.copy(color = Theme.colors.greenStreaming)

    @Composable
    override fun updated(): TextStyle = updated.copy(color = Theme.colors.redUpdate)
    //endregion

    //region Secondary Text Styles
    @Composable
    override fun authorLink(): TextStyle = author().copy(color = Theme.colors.keyColor)

    @Composable
    override fun authorLinkPressed(): TextStyle =
        author().copy(color = Theme.colors.keyColorPressed)

    @Composable
    override fun bodyBold(): TextStyle = body().copy(fontWeight = FontWeight.Bold)

    @Composable
    override fun bodyBoldItalic(): TextStyle = bodyBold().copy(fontStyle = FontStyle.Italic)

    @Composable
    override fun bodyItalic(): TextStyle = body().copy(fontStyle = FontStyle.Italic)

    @Composable
    override fun bodyLink(): TextStyle = body().copy(color = Theme.colors.keyColor)

    @Composable
    override fun bodyLinkPressed(): TextStyle = body().copy(color = Theme.colors.keyColorPressed)

    @Composable
    override fun bodyLinkBold(): TextStyle =
        body().copy(color = Theme.colors.keyColor, fontWeight = FontWeight.Bold)

    @Composable
    override fun bodyLinkBoldPressed(): TextStyle =
        bodyLinkBold().copy(color = Theme.colors.keyColorPressed)

    @Composable
    override fun bodyLinkBoldItalic(): TextStyle = body().copy(
        color = Theme.colors.keyColor,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    )

    @Composable
    override fun bodyLinkBoldItalicPressed(): TextStyle =
        bodyLinkBoldItalic().copy(color = Theme.colors.keyColorPressed)

    @Composable
    override fun bodyLinkItalic(): TextStyle =
        body().copy(color = Theme.colors.keyColor, fontStyle = FontStyle.Italic)

    @Composable
    override fun bodyLinkItalicPressed(): TextStyle =
        bodyLinkItalic().copy(color = Theme.colors.keyColorPressed)

    @Composable
    override fun bodyLargeSansCentered(): TextStyle =
        bodyLargeSans().copy(textAlign = TextAlign.Center)

    @Composable
    override fun bodyLargeSansWhite(): TextStyle = bodyLargeSans().copy(color = Theme.colors.white)

    @Composable
    override fun bodySmall(): TextStyle = body().copy(fontSize = 18.sp, lineHeight = 24.sp)

    @Composable
    override fun bodySmallCentered(): TextStyle = bodySmall().copy(textAlign = TextAlign.Center)

    @Composable
    override fun captionCentered(): TextStyle = caption().copy(textAlign = TextAlign.Center)

    @Composable
    override fun captionGray6(): TextStyle = caption().copy(color = Theme.colors.gray6)

    @Composable
    override fun captionGray6Centered(): TextStyle =
        captionGray6().copy(textAlign = TextAlign.Center)

    @Composable
    override fun captionItalic(): TextStyle = caption().copy(fontStyle = FontStyle.Italic)

    @Composable
    override fun captionLinkRight(): TextStyle = caption()
        .copy(color = Theme.colors.keyColor, textAlign = TextAlign.Right)

    @Composable
    override fun captionLinkRightPressed(): TextStyle =
        captionLinkRight().copy(color = Theme.colors.keyColorPressed)

    @Composable
    override fun captionWhite(): TextStyle = caption().copy(color = Theme.colors.white)

    @Composable
    override fun captionWhiteItalic(): TextStyle = captionWhite().copy(fontStyle = FontStyle.Italic)

    @Composable
    override fun captionBold(): TextStyle = caption().copy(fontWeight = FontWeight.Bold)

    @Composable
    override fun captionBoldGray6(): TextStyle = captionBold().copy(color = Theme.colors.gray6)

    @Composable
    override fun eyebrowBlack(): TextStyle = eyebrow().copy(fontWeight = FontWeight.Black)

    @Composable
    override fun eyebrowHeavyGray8(): TextStyle = eyebrowBlack().copy(color = Theme.colors.gray8)

    @Composable
    override fun eyebrowHeavyWhite(): TextStyle = eyebrowBlack().copy(color = Theme.colors.white)

    @Composable
    override fun eyebrowLargeCentered(): TextStyle = eyebrow()
        .copy(
            fontSize = 14.sp,
            letterSpacing = 1.25.sp,
            lineHeight = 18.sp,
            color = Theme.colors.gray1,
            textAlign = TextAlign.Center
        )

    @Composable
    override fun eyebrowLargeWhiteCentered(): TextStyle =
        eyebrowLargeCentered().copy(color = Theme.colors.white)

    @Composable
    override fun eyebrowMedium(): TextStyle = eyebrow()
        .copy(fontWeight = FontWeight.Medium, color = Theme.colors.gray6)

    @Composable
    override fun eyebrowMediumCentered(): TextStyle =
        eyebrowMedium().copy(textAlign = TextAlign.Center)

    @Composable
    override fun eyebrowMediumTitleCase(): TextStyle =
        eyebrowMedium().copy(letterSpacing = 0.sp)

    @Composable
    override fun eyebrowMediumWhiteCentered(): TextStyle =
        eyebrowMediumCentered().copy(color = Theme.colors.white)

    @Composable
    override fun h2Centered(): TextStyle = h2().copy(textAlign = TextAlign.Center)

    @Composable
    override fun h2SansWhite(): TextStyle = h2Sans().copy(color = Theme.colors.white)

    @Composable
    override fun notificationAppName(): TextStyle =
        notification().copy(fontSize = 11.sp, lineHeight = 13.sp)

    @Composable
    override fun notificationCopy(): TextStyle = notification()

    @Composable
    override fun notificationHeadline(): TextStyle =
        notification().copy(lineHeight = 20.sp, fontWeight = FontWeight.Medium)

    @Composable
    override fun notificationSubCopy(): TextStyle =
        notification().copy(fontSize = 12.sp, lineHeight = 16.sp)

    @Composable
    override fun notificationTitle(): TextStyle =
        notification().copy(fontSize = 14.sp, lineHeight = 18.sp)

    @Composable
    override fun smallTextCenteredSecondaryAction(): TextStyle =
        smallText().copy(color = Theme.colors.keyColor, textAlign = TextAlign.Center)

    @Composable
    override fun smallTextWhite(): TextStyle =
        smallText().copy(color = Theme.colors.white, lineHeight = 18.sp)

    @Composable
    override fun smallTextBold(): TextStyle =
        smallText().copy(fontWeight = FontWeight.Bold, lineHeight = 18.sp)

    @Composable
    override fun smallTextBoldCentered(): TextStyle =
        smallTextBold().copy(textAlign = TextAlign.Center, color = Theme.colors.gray8)

    @Composable
    override fun smallTextBoldOnBoarding(): TextStyle =
        smallText().copy(
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Right,
            color = Theme.colors.keyColorInvert,
            lineHeight = 18.sp
        )

    @Composable
    override fun smallTextBoldWhite(): TextStyle = smallTextBold().copy(color = Theme.colors.white)

    @Composable
    override fun smallTextBoldWhiteCentered(): TextStyle =
        smallTextBoldWhite().copy(textAlign = TextAlign.Center)

    @Composable
    override fun smallTextMedium(): TextStyle = smallText().copy(fontWeight = FontWeight.Medium)

    @Composable
    override fun smallTextMediumCentered(): TextStyle =
        smallTextMedium().copy(textAlign = TextAlign.Center)

    @Composable
    override fun smallTextMediumCenteredGray1(): TextStyle =
        smallTextMediumCentered().copy(color = Theme.colors.gray1)

    @Composable
    override fun smallTextMediumGray7(): TextStyle =
        smallTextMedium().copy(color = Theme.colors.gray7)

    @Composable
    override fun smallTextMediumLink(): TextStyle =
        smallTextMedium().copy(color = Theme.colors.keyColor)

    @Composable
    override fun smallTextMediumLinkPressed(): TextStyle =
        smallTextMedium().copy(color = Theme.colors.keyColorPressed)

    @Composable
    override fun smallTextMediumLinkRight(): TextStyle =
        smallTextMediumLink().copy(textAlign = TextAlign.Right)

    @Composable
    override fun smallTextMediumLinkRightPressed(): TextStyle = smallTextMediumLinkRight().copy(color = Theme.colors.keyColorPressed)

    @Composable
    override fun tabBarActive(): TextStyle = tabBar.copy(color = Theme.colors.keyColor)

    @Composable
    override fun tabBarInactive(): TextStyle = tabBar.copy(color = Theme.colors.gray6)
    //endregion
}