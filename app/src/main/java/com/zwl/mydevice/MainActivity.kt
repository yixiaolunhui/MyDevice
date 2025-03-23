package com.zwl.mydevice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.zwl.mydevice.hongguo.HongGuoActivity
import com.zwl.mydevice.lock.LockActivity
import com.zwl.mydevice.widget.ListMoreTextView
import java.util.regex.Matcher
import java.util.regex.Pattern

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        findViewById<ListMoreTextView>(R.id.tv_more).let {
            val content =
                "高亮股票不为空高亮股票不为空高亮股票不为空高亮股票不为空高亮股票不为空高亮股票不为空高亮股票不为空高亮股票不为空高亮股票不为空高亮股票不为空"
            val sb = StringBuffer(content)
            sb.insert(0, "$${"中国银行"}$ ")
            val spannableString = SpannableString(sb)
            val text = "中国银行"
            if (sb.contains(text)) {
                val escapedText = Pattern.quote(text) // 转义特殊字符
                val pattern: Pattern = Pattern.compile(escapedText) // 这里的正则表达式匹配所有“这”字
                val matcher: Matcher = pattern.matcher(spannableString)
                if (matcher.find()) {
                    // 为每一个匹配的字符应用颜色
                    spannableString.setSpan(
                        StockClickableSpan(it.context),
                        matcher.start() - 1,
                        matcher.end() + 1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            it.text = spannableString
            it.setOnTouchListener(TextViewTouchListener(spannableString))

            it.setOnClickListener {
                Toast.makeText(it.context, "文字点击", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.lock_time).setOnClickListener{
            startActivity(Intent(this@MainActivity, LockActivity::class.java))
        }

        findViewById<Button>(R.id.hg_jump_ad).setOnClickListener{
            startActivity(Intent(this@MainActivity, HongGuoActivity::class.java))
        }
    }

}

class TextViewTouchListener(private val spannable: Spannable) : View.OnTouchListener {
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        val action = event.action
        if (v !is TextView) {
            return false
        }
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            var x = event.x.toInt()
            var y = event.y.toInt()
            x -= v.totalPaddingLeft
            y -= v.totalPaddingTop
            x += v.scrollX
            y += v.scrollY
            val layout = v.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())
            val link = spannable.getSpans(off, off, ClickableSpan::class.java)
            if (link.isNotEmpty()) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(v)
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(
                        spannable,
                        spannable.getSpanStart(link[0]),
                        spannable.getSpanEnd(link[0])
                    )
                }
                return true
            } else {
                Selection.removeSelection(spannable)
            }
        }
        return false
    }
}

/**
 * 文本中的股票点击
 */
class StockClickableSpan(
    val mContext: Context?,
) : ClickableSpan() {
    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        mContext?.apply {
            ds.color = ContextCompat.getColor(this, androidx.core.R.color.call_notification_answer_color)
        }
        ds.isUnderlineText = false
    }

    override fun onClick(view: View) {
        Toast.makeText(view.context, "标签点击", Toast.LENGTH_SHORT).show()
    }
}
