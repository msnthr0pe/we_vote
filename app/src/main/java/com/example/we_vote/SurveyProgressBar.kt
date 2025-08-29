package com.example.we_vote

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes

class SurveyProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private var progress: Int = 0
        set(value) {
            field = if (value > 100) {100; return} else value
            field = if (value < 5 && value != 0) {5} else value
        }
    private var progressColor: Int = Color.BLACK
    private var spaceColor: Int = Color.WHITE
    private var cornerRadiusDp: Float = 8f

    private lateinit var progressView: View
    private lateinit var emptyView: View

    init {
        orientation = HORIZONTAL
        setupView()

        val typedValue = TypedValue()
        val theme = context.theme
        val hasColorPrimary = theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)
        val colorPrimary = if (hasColorPrimary) typedValue.data else ContextCompat.getColor(context, R.color.light_yellow)

        val hasColorSecondary = theme.resolveAttribute(R.attr.ViewBackgroundFill, typedValue, true)
        val colorSecondary = if (hasColorSecondary) typedValue.data else Color.WHITE


        attrs?.let {
            context.withStyledAttributes(it, R.styleable.SurveyProgressBar, 0, 0) {
                progress = getInt(R.styleable.SurveyProgressBar_progress, 65)
                progressColor = getColor(R.styleable.SurveyProgressBar_progressColor, colorPrimary)
                spaceColor = getColor(R.styleable.SurveyProgressBar_spaceColor, colorSecondary)
                cornerRadiusDp = getDimension(R.styleable.SurveyProgressBar_cornerRadius, dpToPx(8f).toFloat())
            }
            foreground = ContextCompat.getDrawable(context, R.drawable.transparent_frame)
            setPadding(0, 0, 0, 0)
        }

//        if (isInEditMode) {
//            setPreviewData(65, colorPrimary, colorSecondary)
//        } else {
//            updateView()
//        }
        updateView()
    }

    private fun setupView() {
        emptyView = View(context).apply {
            layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 100f)
            background = createFullRoundDrawable(spaceColor)
        }

        progressView = View(context).apply {
            layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 0f)
            background = createRoundedDrawable(progressColor, isLeftSide = false)
        }

        addView(progressView)
        addView(emptyView)
    }

    private fun updateView() {
        val progressParams = progressView.layoutParams as LayoutParams
        progressParams.weight = progress.toFloat()

        val emptyParams = emptyView.layoutParams as LayoutParams
        emptyParams.weight = (100 - progress).toFloat()

        progressView.layoutParams = progressParams
        emptyView.layoutParams = emptyParams

        if (progress == 100 || progress == 0) {
            progressView.background = createFullRoundDrawable(progressColor)
            emptyView.background = createFullRoundDrawable(spaceColor)
        } else {
            progressView.background = createRoundedDrawable(progressColor, isLeftSide = true)
            emptyView.background = createRoundedDrawable(spaceColor, isLeftSide = false)
        }

        requestLayout()
    }

    fun setData(progress: Int, color: Int, spaceColor: Int) {
        this.progress = progress
        this.progressColor = color
        this.spaceColor = spaceColor
        updateView()
    }

    private fun setPreviewData(progress: Int, color: Int, spaceColor: Int) {
        setData(progress, color, spaceColor)
    }

    private fun createRoundedDrawable(color: Int, isLeftSide: Boolean): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.setColor(color)
        val radiusPx = dpToPx(cornerRadiusDp)
        if (isLeftSide) {
            drawable.cornerRadii = floatArrayOf(
                radiusPx.toFloat(), radiusPx.toFloat(), // top-left
                0f, 0f,                                 // top-right
                0f, 0f,                                 // bottom-right
                radiusPx.toFloat(), radiusPx.toFloat()  // bottom-left
            )
        } else {
            drawable.cornerRadii = floatArrayOf(
                0f, 0f,                                 // top-left
                radiusPx.toFloat(), radiusPx.toFloat(), // top-right
                radiusPx.toFloat(), radiusPx.toFloat(), // bottom-right
                0f, 0f,                                 // bottom-left
            )
        }
        return drawable
    }

    private fun createFullRoundDrawable(color: Int): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.setColor(color)
        val radiusPx = dpToPx(cornerRadiusDp)
        drawable.cornerRadius = radiusPx.toFloat()
        return drawable
    }

    private fun dpToPx(dp: Float): Int =
        (dp * resources.displayMetrics.density).toInt()
}
