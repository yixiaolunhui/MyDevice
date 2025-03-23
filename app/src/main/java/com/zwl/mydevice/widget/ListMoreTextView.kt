package com.zwl.mydevice.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.StaticLayout
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.zwl.mydevice.R
import kotlin.math.ceil

class ListMoreTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var maxLine: Int = Int.MAX_VALUE
    private var moreTextSize: Int = 13
    private var moreText: String? = "...全文"
    private var moreTextColor: Int = Color.BLUE
    private var mPaint: Paint? = null


    init {
        val array = getContext().obtainStyledAttributes(attrs, R.styleable.MoreTextViewStyle, defStyleAttr, 0)
        maxLine = array.getInt(R.styleable.MoreTextViewStyle_more_text_maxLines, Int.MAX_VALUE)
        moreText = array.getString(R.styleable.MoreTextViewStyle_more_text)
        moreTextSize = array.getInteger(R.styleable.MoreTextViewStyle_more_text_size, 13)
        moreTextColor = array.getColor(R.styleable.MoreTextViewStyle_more_text_color, Color.BLACK)
        array.recycle()
        init()
    }

    private fun init() {
        mPaint = paint
    }

    /**
     * 设置最大行数
     */
    fun setMaxLine(maxLine: Int) {
        this.maxLine = maxLine
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        updateMoreTextView()
    }

    private fun updateMoreTextView() {
        try {
            if (lineCount > maxLine) {
                val (layout, stringBuilder, sb) = clipContent()
                stringBuilder.append(sb)
                setMeasuredDimension(measuredWidth, getDesiredHeight(layout))
                text = stringBuilder
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 裁剪内容
     */
    private fun clipContent(): Triple<Layout, SpannableStringBuilder, SpannableString> {
        var offset = 1
        val layout = layout
        val staticLayout = StaticLayout(
            text, layout.paint, layout.width, Layout.Alignment.ALIGN_NORMAL, layout.spacingMultiplier, layout.spacingAdd, false
        )
        val indexEnd = staticLayout.getLineEnd(maxLine - 1)
        val tempText = text.subSequence(0, indexEnd)
        var offsetWidth = layout.paint.measureText(tempText[indexEnd - 1].toString()).toInt()
        val moreWidth = ceil(layout.paint.measureText(moreText).toDouble()).toInt()
        //表情字节个数
        var countEmoji = 0
        while (indexEnd > offset && offsetWidth <= moreWidth) {
            //当前字节是否位表情
            val isEmoji = isEmojiCharacter(tempText[indexEnd - offset])
            if (isEmoji) {
                countEmoji += 1
            }
            offset++
            val pair = getOffsetWidth(
                indexEnd, offset, tempText, countEmoji, offsetWidth, layout, moreWidth
            )
            offset = pair.first
            offsetWidth = pair.second
        }
        val ssbShrink = tempText.subSequence(0, indexEnd - offset)
        val stringBuilder = SpannableStringBuilder(ssbShrink)
        val sb = SpannableString(moreText)

        // 动态计算 Span 的起始位置
        val spanStart = (sb.length - 2).coerceAtLeast(0) // 确保 spanStart 不小于 0
        val spanEnd = sb.length

        // 确保起始位置有效
        if (spanStart in 0..<spanEnd) {
            sb.setSpan(
                ForegroundColorSpan(moreTextColor), spanStart, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            //设置字体大小
            sb.setSpan(
                AbsoluteSizeSpan(moreTextSize, true), spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return Triple(layout, stringBuilder, sb)
    }

    private fun getOffsetWidth(
        indexEnd: Int, offset: Int, tempText: CharSequence, countEmoji: Int, offsetWidth: Int, layout: Layout, moreWidth: Int,
    ): Pair<Int, Int> {
        var offset1 = offset
        var offsetWidth1 = offsetWidth
        if (indexEnd > offset1) {
            val text = tempText[indexEnd - offset1 - 1].toString().trim()
            if (text.isNotEmpty() && countEmoji % 2 == 0) {
                val charText = tempText[indexEnd - offset1]
                offsetWidth1 += layout.paint.measureText(charText.toString()).toInt()
                //一个表情两个字符，避免截取一半字符出现乱码或者显示不全...全文
                if (offsetWidth1 > moreWidth && isEmojiCharacter(charText)) {
                    offset1++
                }
            }
        } else {
            val charText = tempText[indexEnd - offset1]
            offsetWidth1 += layout.paint.measureText(charText.toString()).toInt()
        }
        return Pair(offset1, offsetWidth1)
    }

    /**
     * 获取内容高度
     */
    private fun getDesiredHeight(layout: Layout?): Int {
        if (layout == null) return 0
        val lineTop: Int
        val lineCount = layout.lineCount
        val compoundPaddingTop = compoundPaddingTop + compoundPaddingBottom - lineSpacingExtra.toInt()
        lineTop = when {
            lineCount > maxLine -> {
                layout.getLineTop(maxLine)
            }

            else -> {
                layout.getLineTop(lineCount)
            }
        }
        return (lineTop + compoundPaddingTop).coerceAtLeast(suggestedMinimumHeight)
    }


    private fun isEmojiCharacter(codePoint: Char): Boolean {
        return !(codePoint.toInt() == 0x0 || codePoint.toInt() == 0x9 || codePoint.toInt() == 0xA || codePoint.toInt() == 0xD || codePoint.toInt() in 0x20..0xD7FF || codePoint.toInt() in 0xE000..0xFFFD || codePoint.toInt() in 0x10000..0x10FFFF)
    }

}