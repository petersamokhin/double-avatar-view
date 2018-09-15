package com.petersamokhin.android.doubleavatarview

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.webkit.URLUtil
import android.widget.*
import java.net.URL
import kotlin.concurrent.thread
import kotlin.math.min

/**
 * Instagram-like double avatar view.
 *
 * Created: 07/09/2018 at 00:58
 * @author PeterSamokhin, https://petersamokhin.com/
 */
@Suppress("unused")
class DoubleAvatarView @JvmOverloads
constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attributeSet, defStyleAttr) {

    /**
     * Paint for cropping
     */
    private val paint = Paint(LAYER_TYPE_SOFTWARE)

    /**
     * Simple paint for bitmaps
     */
    private val simplePaint = Paint(LAYER_TYPE_SOFTWARE)

    /**
     * Size of first (back) image, calculated based on view size
     */
    private var firstSize = -1f

    /**
     * Crop rectangle
     */
    private var rect: RectF? = null

    private var backBitmap: Bitmap? = null
    private var frontBitmap: Bitmap? = null

    private var config = DEFAULT_CONFIG

    init {

        // extends FrameLayout for editor view image stub
        if (isInEditMode) {
            addView(ImageView(context).apply {
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.logo))
            })
        } else {
            setWillNotDraw(false)
            setLayerType(LAYER_TYPE_SOFTWARE, null)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

            context.obtainStyledAttributes(attributeSet, R.styleable.DoubleAvatarView).apply {
                val hOffset = getFloat(R.styleable.DoubleAvatarView_horizontal_offset, DEFAULT_CONFIG.hOffset)
                val vOffset = getFloat(R.styleable.DoubleAvatarView_vertical_offset, DEFAULT_CONFIG.vOffset)
                val secondSizeCoeff = getFloat(R.styleable.DoubleAvatarView_second_size_coeff, DEFAULT_CONFIG.secondSizeCoeff)
                val cutSizeCoeff = getFloat(R.styleable.DoubleAvatarView_cut_size_coeff, DEFAULT_CONFIG.cutSizeCoeff)

                config = config.copy(hOffset = hOffset, vOffset = vOffset, secondSizeCoeff = secondSizeCoeff, cutSizeCoeff = cutSizeCoeff)

                recycle()
            }

            afterLayout {
                applyConfig()
            }
        }
    }

    /**
     * Update config and redraw view
     */
    fun updateConfig(c: Config) {
        config = c
        applyConfig()
    }

    /**
     * Recalculate first image size and other
     */
    private fun applyConfig() {
        firstSize = min(width / (config.hOffset + config.secondSizeCoeff), height / (config.vOffset + config.secondSizeCoeff))

        rect = RectF(
            firstSize * config.hOffset - firstSize * (config.cutSizeCoeff - 1),
            firstSize * config.vOffset - firstSize * (config.cutSizeCoeff - 1),
            firstSize + firstSize * config.hOffset + firstSize * (config.cutSizeCoeff * config.secondSizeCoeff - 1),
            firstSize + firstSize * config.vOffset + firstSize * (config.cutSizeCoeff * config.secondSizeCoeff - 1)
        )

        if (URLUtil.isValidUrl(config.firstImage) && URLUtil.isValidUrl(config.secondImage)) {
            thread {
                backBitmap = URL(config.firstImage).readBytes().toBitmap().cropCenter().circleCrop(firstSize.toInt())
                frontBitmap = URL(config.secondImage).readBytes().toBitmap().cropCenter().circleCrop((firstSize * config.secondSizeCoeff).toInt())

                postInvalidate()
            }
        } else {
            postInvalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        with(canvas) {
            backBitmap?.also {
                drawBitmap(it, 0f, 0f, simplePaint)
            }
            rect?.also {
                drawRoundRect(it, it.width(), it.height(), paint)
            }
            frontBitmap?.also {
                drawBitmap(it, firstSize * config.hOffset, firstSize * config.vOffset, simplePaint)
            }
        }
    }

    fun getConfig() = config

    /**
     * Double Avatar View drawing configuration.
     *
     * @property firstImage First (back) image
     * @property secondImage Second (front) image
     * @property hOffset Horizontal offset of second image in percents of first image's width
     * @property vOffset Vertical offset of second image in percents of first image's height
     * @property secondSizeCoeff Second image's radius multiplier
     * @property cutSizeCoeff Second image's background crop radius multiplier
     */
    data class Config(
        val firstImage: String,
        val secondImage: String,
        val hOffset: Float,
        val vOffset: Float,
        val secondSizeCoeff: Float,
        val cutSizeCoeff: Float
    )

    companion object {
        @JvmField
        val DEFAULT_CONFIG = Config("", "", .45f, .45f, 1f, 1.115f)
    }
}