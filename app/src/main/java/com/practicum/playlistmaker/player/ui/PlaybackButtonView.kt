package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.practicum.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val playBitmap: Bitmap?
    private val pauseBitmap: Bitmap?
    private var buttonState: Bitmap?
    private var imageRect = RectF(0f, 0f, 0f, 0f)

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomPlayerButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {

                playBitmap = getDrawable(R.styleable.CustomPlayerButtonView_playButton)?.toBitmap()
                pauseBitmap = getDrawable(R.styleable.CustomPlayerButtonView_pauseButton)?.toBitmap()
                buttonState = playBitmap

            } finally {
                recycle()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                buttonState = if (buttonState == playBitmap){
                    pauseBitmap
                }else{
                    playBitmap
                }
                invalidate()
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        buttonState?.let {
            canvas.drawBitmap(it,null,imageRect,null)
        }
    }

    fun endOfPlaying() {
        if(buttonState == pauseBitmap){
            buttonState = playBitmap
        }
        invalidate()
    }

}