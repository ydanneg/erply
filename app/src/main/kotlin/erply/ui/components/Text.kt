package erply.ui.components

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

@Composable
private fun BaseText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign? = null,
    color: Color = LocalContentColor.current,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        fontWeight = fontWeight,
        textAlign = textAlign,
        color = color,
        overflow = overflow,
        maxLines = maxLines,
    )
}
//
//@Composable
//fun H1(
//        text: String,
//        modifier: Modifier = Modifier,
//        fontWeight: FontWeight = FontWeight.Normal,
//        textAlign: TextAlign? = null,
//        color: Color = LocalContentColor.current,
//        overflow: TextOverflow = TextOverflow.Clip,
//        maxLines: Int = Int.MAX_VALUE
//) {
//    BaseText(
//            text = text,
//            modifier = modifier,
//            style = MaterialTheme.typography.headlineLarge,
//            fontWeight = fontWeight,
//            textAlign = textAlign,
//            color = color,
//            overflow = overflow,
//            maxLines = maxLines
//    )
//}
//
//
//@Composable
//fun H2(
//        text: String,
//        modifier: Modifier = Modifier,
//        fontWeight: FontWeight = FontWeight.Normal,
//        textAlign: TextAlign? = null,
//        color: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
//        overflow: TextOverflow = TextOverflow.Clip,
//        maxLines: Int = Int.MAX_VALUE
//) {
//    BaseText(
//            text = text,
//            modifier = modifier,
//            style = MaterialTheme.typography.h2,
//            fontWeight = fontWeight,
//            textAlign = textAlign,
//            color = color,
//            overflow = overflow,
//            maxLines = maxLines
//    )
//}
//
//
//@Composable
//fun H3(
//        text: String,
//        modifier: Modifier = Modifier,
//        fontWeight: FontWeight = FontWeight.Normal,
//        textAlign: TextAlign? = null,
//        color: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
//        overflow: TextOverflow = TextOverflow.Clip,
//        maxLines: Int = Int.MAX_VALUE
//) {
//    BaseText(
//            text = text,
//            modifier = modifier,
//            style = MaterialTheme.typography.h3,
//            fontWeight = fontWeight,
//            textAlign = textAlign,
//            color = color,
//            overflow = overflow,
//            maxLines = maxLines
//    )
//}
//
//
//@Composable
//fun H4(
//        text: String,
//        modifier: Modifier = Modifier,
//        fontWeight: FontWeight = FontWeight.Normal,
//        textAlign: TextAlign? = null,
//        color: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
//        overflow: TextOverflow = TextOverflow.Clip,
//        maxLines: Int = Int.MAX_VALUE
//) {
//    BaseText(
//            text = text,
//            modifier = modifier,
//            style = MaterialTheme.typography.h4,
//            fontWeight = fontWeight,
//            textAlign = textAlign,
//            color = color,
//            overflow = overflow,
//            maxLines = maxLines
//    )
//}
//
//
//@Composable
//fun H5(
//        text: String,
//        modifier: Modifier = Modifier,
//        fontWeight: FontWeight = FontWeight.Normal,
//        textAlign: TextAlign? = null,
//        color: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
//        overflow: TextOverflow = TextOverflow.Clip,
//        maxLines: Int = Int.MAX_VALUE
//) {
//    BaseText(
//            text = text,
//            modifier = modifier,
//            style = MaterialTheme.typography.h5,
//            fontWeight = fontWeight,
//            textAlign = textAlign,
//            color = color,
//            overflow = overflow,
//            maxLines = maxLines
//    )
//}
//
//
//@Composable
//fun H6(
//        text: String,
//        modifier: Modifier = Modifier,
//        fontWeight: FontWeight = FontWeight.Normal,
//        textAlign: TextAlign? = null,
//        color: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
//        overflow: TextOverflow = TextOverflow.Clip,
//        maxLines: Int = Int.MAX_VALUE
//) {
//    BaseText(
//            text = text,
//            modifier = modifier,
//            style = MaterialTheme.typography.h6,
//            fontWeight = fontWeight,
//            textAlign = textAlign,
//            color = color,
//            overflow = overflow,
//            maxLines = maxLines
//    )
//}
//
//@Composable
//fun Body1(
//        text: String,
//        modifier: Modifier = Modifier,
//        fontWeight: FontWeight = FontWeight.Normal,
//        textAlign: TextAlign? = null,
//        color: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
//        overflow: TextOverflow = TextOverflow.Clip,
//        maxLines: Int = Int.MAX_VALUE
//) {
//    BaseText(
//            text = text,
//            modifier = modifier,
//            style = MaterialTheme.typography.body1,
//            fontWeight = fontWeight,
//            textAlign = textAlign,
//            color = color,
//            overflow = overflow,
//            maxLines = maxLines
//    )
//}
//
//@Composable
//fun Body2(
//        text: String,
//        modifier: Modifier = Modifier,
//        fontWeight: FontWeight = FontWeight.Normal,
//        textAlign: TextAlign? = null,
//        color: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
//        overflow: TextOverflow = TextOverflow.Clip,
//        maxLines: Int = Int.MAX_VALUE
//) {
//    BaseText(
//            text = text,
//            modifier = modifier,
//            style = MaterialTheme.typography.body2,
//            fontWeight = fontWeight,
//            textAlign = textAlign,
//            color = color,
//            overflow = overflow,
//            maxLines = maxLines
//    )
//}
//
//@Composable
//fun Subtitle1(
//        text: String,
//        modifier: Modifier = Modifier,
//        fontWeight: FontWeight = FontWeight.Normal,
//        textAlign: TextAlign? = null,
//        color: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
//        overflow: TextOverflow = TextOverflow.Clip,
//        maxLines: Int = Int.MAX_VALUE
//) {
//    BaseText(
//            text = text,
//            modifier = modifier,
//            style = MaterialTheme.typography.subtitle1,
//            fontWeight = fontWeight,
//            textAlign = textAlign,
//            color = color,
//            overflow = overflow,
//            maxLines = maxLines
//    )
//}
//
//@Composable
//fun Subtitle2(
//        text: String,
//        modifier: Modifier = Modifier,
//        fontWeight: FontWeight = FontWeight.Normal,
//        textAlign: TextAlign? = null,
//        color: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
//        overflow: TextOverflow = TextOverflow.Clip,
//        maxLines: Int = Int.MAX_VALUE
//) {
//    BaseText(
//            text = text,
//            modifier = modifier,
//            style = MaterialTheme.typography.subtitle2,
//            fontWeight = fontWeight,
//            textAlign = textAlign,
//            color = color,
//            overflow = overflow,
//            maxLines = maxLines
//    )
//}
//
//
//@Composable
//fun Caption(
//        text: String,
//        modifier: Modifier = Modifier,
//        fontWeight: FontWeight = FontWeight.Normal,
//        textAlign: TextAlign? = null,
//        color: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
//        overflow: TextOverflow = TextOverflow.Clip,
//        maxLines: Int = Int.MAX_VALUE
//) {
//    BaseText(
//            text = text,
//            modifier = modifier,
//            style = MaterialTheme.typography.caption,
//            fontWeight = fontWeight,
//            textAlign = textAlign,
//            color = color,
//            overflow = overflow,
//            maxLines = maxLines
//    )
//}
//
//
//@Composable
//fun ButtonText(
//        text: String,
//        modifier: Modifier = Modifier,
//        fontWeight: FontWeight = FontWeight.Normal,
//        textAlign: TextAlign? = null,
//        color: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
//        overflow: TextOverflow = TextOverflow.Clip,
//        maxLines: Int = Int.MAX_VALUE
//) {
//    BaseText(
//            text = text,
//            modifier = modifier,
//            style = MaterialTheme.typography.button,
//            fontWeight = fontWeight,
//            textAlign = textAlign,
//            color = color,
//            overflow = overflow,
//            maxLines = maxLines
//    )
//}