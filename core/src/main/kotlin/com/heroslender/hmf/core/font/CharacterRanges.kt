package com.heroslender.hmf.core.font


/**
 * https://www.ling.upenn.edu/courses/Spring_2003/ling538/UnicodeRanges.html
 *
 *    val UBUNTU_MONO_16: Font = FontParser.getFontFromResources(
 *        asset = "fonts/UbuntuMono-Regular.ttf",
 *        fontName = "Ubuntu Mono",
 *        size = 16,
 *    )
 *
 *    val UBUNTU_MONO_16_CHINESE: Font = FontParser.getFontFromResources(
 *        asset = "fonts/UbuntuMono-Regular.ttf",
 *        fontName = "Ubuntu Mono",
 *        size = 16,
 *        charsToLoad = CharacterRanges.DefaultChars + CharacterRanges.ChineseChars,
 *    )
 */

object CharacterRanges {
    val `Basic Latin` = '\u0000'..'\u007F'
    val `C1 Controls and Latin-1 Supplement` = '\u0080'..'\u00FF'
    val `Latin Extended-A` = '\u0100'..'\u017F'
    val `Latin Extended-B` = '\u0180'..'\u024F'
    val `IPA Extensions` = '\u0250'..'\u02AF'
    val `Spacing Modifier Letters` = '\u02B0'..'\u02FF'
    val `Combining Diacritical Marks` = '\u0300'..'\u036F'
    val `Greek or Coptic` = '\u0370'..'\u03FF'
    val Cyrillic = '\u0400'..'\u04FF'
    val `Cyrillic Supplement` = '\u0500'..'\u052F'
    val Armenian = '\u0530'..'\u058F'
    val Hebrew = '\u0590'..'\u05FF'
    val Arabic = '\u0600'..'\u06FF'
    val Syriac = '\u0700'..'\u074F'
    val Thaana = '\u0780'..'\u07BF'
    val Devanagari = '\u0900'..'\u097F'
    val Chinese = '\u4E00'..'\u9FAF'


    val DefaultChars = (`Basic Latin` + `Latin Extended-A` + `Latin Extended-B`)
}