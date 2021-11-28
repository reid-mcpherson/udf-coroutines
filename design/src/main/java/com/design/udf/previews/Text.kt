package com.design.udf.previews

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.design.udf.Typography
import com.design.udf.TypographyImpl

private const val VIEW_WIDTH = 150

private val typography: Typography = TypographyImpl

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun Author() {
    Text(text = "Author", style = typography.author())
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun AuthorLink() {
    Text(text = "Author Link", style = typography.authorLink())
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun AuthorLinkPressed() {
    Text(
        text = "Author Link Pressed",
        style = typography.authorLinkPressed()
    )
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun Body() {
    Text(text = "Body", style = typography.body())
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun BodyBold() {
    Text(text = "Body Bold", style = typography.bodyBold())
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun BodyBoldItalic() {
    Text(
        text = "Body Bold Italic",
        style = typography.bodyBoldItalic()
    )
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun BodyItalic() {
    Text(text = "Body Italic", style = typography.bodyItalic())
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun BodyLink() {
    Text(text = "Body Link", style = typography.bodyLink())
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun BodyLinkPressed() {
    Text(
        text = "Body Link Pressed",
        style = typography.bodyLinkPressed()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun BodyLinkBold() {
    Text(
        text = "Body Link Bold",
        style = typography.bodyLinkBold()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun BodyLinkBoldPressed() {
    Text(
        text = "Body Link Bold Pressed",
        style = typography.bodyLinkBoldPressed()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun BodyLinkBoldItalic() {
    Text(
        text = "Body Link Bold Italic",
        style = typography.bodyLinkBoldItalic()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun BodyLinkBoldItalicPressed() {
    Text(
        text = "Body Link Bold Italic Pressed",
        style = typography.bodyLinkBoldItalicPressed()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun BodyLinkItalic() {
    Text(
        text = "Body Link Italic",
        style = typography.bodyLinkItalic()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun BodyLinkItalicPressed() {
    Text(
        text = "Body Link Italic Pressed",
        style = typography.bodyLinkItalicPressed()
    )
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun BodyLargeSans() {
    Text(text = "Body Large Sans", style = typography.bodyLargeSans())
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun BodyLargeSansCentered() {
    Text(
        text = "Body Large Sans Centered",
        style = typography.bodyLargeSansCentered()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun BodyLargeSansWhite() {
    Text(
        text = "Body Large Sans White",
        style = typography.bodyLargeSansWhite()
    )
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun BodySmall() {
    Text(text = "Body Small", style = typography.bodySmall())
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun BodySmallCentered() {
    Text(
        text = "Body Small Centered",
        style = typography.bodySmallCentered()
    )
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun Caption() {
    Text(text = "Caption", style = typography.caption())
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun CaptionCentered() {
    Text(text = "Caption", style = typography.captionCentered())
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun CaptionGray6() {
    Text(text = "Caption Gray 6", style = typography.captionGray6())
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun CaptionGray6Centered() {
    Text(
        text = "Caption Gray 6",
        style = typography.captionGray6Centered()
    )
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun CaptionItalic() {
    Text(text = "Caption Italic", style = typography.captionItalic())
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun CaptionLinkRight() {
    Text(
        text = "Caption Link Right",
        style = typography.captionLinkRight()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun CaptionLinkRightPressed() {
    Text(
        text = "Caption Link Right Pressed",
        style = typography.captionLinkRightPressed()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun CaptionWhite() {
    Text(text = "Caption White", style = typography.captionWhite())
}


@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun CaptionWhiteItalic() {
    Text(
        text = "Caption White Italic",
        style = typography.captionWhiteItalic()
    )
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun CaptionBold() {
    Text(text = "Caption Bold", style = typography.captionBold())
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun CaptionBoldGray6() {
    Text(
        text = "Caption Bold Gray6",
        style = typography.captionBoldGray6()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun Contribute() {
    Text(text = "Contribute", style = typography.contribute())
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun EyebrowBlack() {
    Text(
        text = "Eyebrow Black".uppercase(),
        style = typography.eyebrowBlack()
    )
}


@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun EyebrowHeavyGray8() {
    Text(
        text = "Eyebrow Heavy Gray 8".uppercase(),
        style = typography.eyebrowHeavyGray8()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun EyebrowHeavyWhite() {
    Text(
        text = "Eyebrow Heavy White".uppercase(),
        style = typography.eyebrowHeavyWhite()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun EyebrowLargeCentered() {
    Text(
        text = "Eyebrow Large Centered".uppercase(),
        style = typography.eyebrowLargeCentered()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun EyebrowLargeWhiteCentered() {
    Text(
        text = "Eyebrow Large White Centered".uppercase(),
        style = typography.eyebrowLargeWhiteCentered()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun EyebrowMedium() {
    Text(
        text = "Eyebrow Medium".uppercase(),
        style = typography.eyebrowMedium()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun EyebrowMediumCentered() {
    Text(
        text = "Eyebrow Medium Centered".uppercase(),
        style = typography.eyebrowMediumCentered()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun EyebrowMediumTitleCase() {
    Text(
        text = "Eyebrow Medium Title Case",
        style = typography.eyebrowMediumTitleCase()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun EyebrowMediumWhiteCentered() {
    Text(
        text = "Eyebrow Medium White Centered".uppercase(),
        style = typography.eyebrowMediumWhiteCentered()
    )
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun H1() {
    Text(text = "H1", style = typography.h1())
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun H2() {
    Text(text = "H2", style = typography.h2())
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun H2Centered() {
    Text(text = "H2", style = typography.h2Centered())
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun H2Sans() {
    Text(text = "H2 Sans", style = typography.h2Sans())
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun H2SansWhite() {
    Text(text = "H2 Sans White", style = typography.h2SansWhite())
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun H3() {
    Text(text = "H3", style = typography.h3())
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun ImageCredit() {
    Text(text = "Image Credit", style = typography.imageCredit())
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun InTheNews() {
    Text(text = "In The News", style = typography.inTheNews())
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun InlineAudioPlayerTime() {
    Text(text = "Inline Audio Player Time", style = typography.inlineAudioPlayerTime())
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun NotificationAppName() {
    Text(
        text = "Notification App Name",
        style = typography.notificationAppName()
    )
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun NotificationCopy() {
    Text(text = "Notification Copy", style = typography.notificationCopy())
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun NotificationHeadline() {
    Text(
        text = "Notification Headline",
        style = typography.notificationHeadline()
    )
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun NotificationSubCopy() {
    Text(
        text = "Notification Sub Copy",
        style = typography.notificationSubCopy()
    )
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun NotificationTitle() {
    Text(
        text = "Notification Title",
        style = typography.notificationTitle()
    )
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun SmallText() {
    Text(text = "Small Text", style = typography.smallText())
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun SmallTextCenteredSecondaryAction() {
    Text(
        text = "Small Text Centered Secondary Action",
        style = typography.smallTextCenteredSecondaryAction()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun SmallTextWhite() {
    Text(
        text = "Small Text White",
        style = typography.smallTextWhite()
    )
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun SmallTextBold() {
    Text(
        text = "Small Text Bold",
        style = typography.smallTextBold()
    )
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun SmallTextBoldCentered() {
    Text(
        text = "Small Text Bold Centered",
        style = typography.smallTextBoldCentered()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun SmallTextBoldOnBoarding() {
    Text(
        text = "Small Text Bold OnBoarding".uppercase(),
        style = typography.smallTextBoldOnBoarding()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun SmallTextBoldWhite() {
    Text(
        text = "Small Text Bold White",
        style = typography.smallTextBoldWhite()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun SmallTextBoldWhiteCentered() {
    Text(
        text = "Small Text Bold White Centered",
        style = typography.smallTextBoldWhiteCentered()
    )
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun SmallTextMedium() {
    Text(
        text = "Small Text Medium",
        style = typography.smallTextMedium()
    )
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun SmallTextMediumCentered() {
    Text(
        text = "Small Text Medium Centered",
        style = typography.smallTextMediumCentered()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun SmallTextMediumCenteredGray1() {
    Text(
        text = "Small Text Medium Centered Gray1",
        style = typography.smallTextMediumCenteredGray1()
    )
}

@Preview(showBackground = true, widthDp = VIEW_WIDTH)
@Composable
private fun SmallTextMediumGray7() {
    Text(
        text = "Small Text Medium Gray7",
        style = typography.smallTextMediumGray7()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun SmallTextMediumLink() {
    Text(
        text = "Small Text Medium Link",
        style = typography.smallTextMediumLink()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun SmallTextMediumLinkPressed() {
    Text(
        text = "Small Text Medium Link Pressed",
        style = typography.smallTextMediumLinkPressed()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun SmallTextMediumLinkRight() {
    Text(
        text = "Small Text Medium Link Right",
        style = typography.smallTextMediumLinkRight()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun SmallTextMediumLinkRightPressed() {
    Text(
        text = "Small Text Medium Link Right Pressed",
        style = typography.smallTextMediumLinkRightPressed()
    )
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun StreamingNow() {
    Text(text = "Streaming Now", style = typography.streamingNow())
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun TabBarActive() {
    Text(text = "Tab Bar Active", style = typography.tabBarActive())
}

@Preview(widthDp = VIEW_WIDTH)
@Composable
private fun TabBarInactive() {
    Text(text = "Tab Bar Inactive", style = typography.tabBarInactive())
}

@Preview
@Composable
private fun Updated() {
    Text(text = "Updated", style = typography.updated())
}