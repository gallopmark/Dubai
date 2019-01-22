package com.uroad.dubai.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.TextView

import com.uroad.dubai.R


class MyLetterSortView : View {

    private var onTouchingLetterChangedListener: OnTouchingLetterChangedListener? = null
    private var choose = -1
    private val paint = Paint()

    private var mTextDialog: TextView? = null

    fun setTextView(mTextDialog: TextView) {
        this.mTextDialog = mTextDialog
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val height = height
        val width = width
        val singleHeight = height / b.size

        for (i in b.indices) {
            paint.color = Color.parseColor("#9da0a4")
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.isAntiAlias = true
            //paint.setTextSize(PixelUtil.sp2px(12))
            paint.textSize = 25f

            if (i == choose) {
                paint.color = Color.parseColor("#3399ff")
                paint.isFakeBoldText = true
            }
            val xPos = width / 2 - paint.measureText(b[i]) / 2
            val yPos = (singleHeight * i + singleHeight).toFloat()
            canvas.drawText(b[i], xPos, yPos, paint)
            paint.reset()
        }

    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val y = event.y
        val oldChoose = choose
        val listener = onTouchingLetterChangedListener
        val c = (y / height * b.size).toInt()

        when (action) {
            MotionEvent.ACTION_UP -> {
                background = ColorDrawable(0x00000000)
                choose = -1
                invalidate()
                if (mTextDialog != null) {
                    mTextDialog!!.visibility = View.INVISIBLE
                }
            }

            else -> {
                setBackgroundResource(R.drawable.letter_sort_background)
                if (oldChoose != c) {
                    if (c >= 0 && c < b.size) {
                        listener?.onTouchingLetterChanged(b[c])
                        if (mTextDialog != null) {
                            mTextDialog!!.text = b[c]
                            mTextDialog!!.visibility = View.VISIBLE
                        }

                        choose = c
                        invalidate()
                    }
                }
            }
        }
        return true
    }

    fun setOnTouchingLetterChangedListener(
            onTouchingLetterChangedListener: OnTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener
    }


    interface OnTouchingLetterChangedListener {
        fun onTouchingLetterChanged(s: String)
    }

    companion object {
        var b = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
                "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#")
    }

}
