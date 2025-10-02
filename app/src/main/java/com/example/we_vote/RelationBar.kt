package com.example.we_vote

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes

class RelationBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    var progress: Int = 0
        set(value) {
            field = if (value > 100) {100; return} else value
            field = if (value < 5 && value != 0) {5} else value
            percentText = field.toString()

            updateView()
        }

    var titleText: String = context.getString(R.string.default_value)
        set(value) {
            field = value

            updateView()
        }

    private var percentText: String = context.getString(R.string.default_percent)
    private var progressColor: Int = Color.BLACK
    private var spaceColor: Int = Color.WHITE
    private var cornerRadiusDp: Float = 8f

    private lateinit var titleTextView: TextView
    private lateinit var percentTextView: TextView
    private lateinit var progressView: View
    private lateinit var emptyView: View

    init {
        orientation = VERTICAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 74)
        setupView()

        val typedValue = TypedValue()
        val theme = context.theme
        val hasColorPrimary = theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)
        val colorPrimary = if (hasColorPrimary) typedValue.data else ContextCompat.getColor(context, R.color.light_yellow)

        val hasColorSecondary = theme.resolveAttribute(R.attr.ViewBackgroundFill, typedValue, true)
        val colorSecondary = if (hasColorSecondary) typedValue.data else Color.WHITE

        attrs?.let {
            context.withStyledAttributes(it, R.styleable.RelationBar, 0, 0) {
                percentText = getInt(R.styleable.RelationBar_progress, 65).toString()
                progressColor = getColor(R.styleable.RelationBar_progressColor, colorPrimary)
                titleText = getString(R.styleable.RelationBar_text) ?: context.getString(R.string.default_value)
                spaceColor = getColor(R.styleable.RelationBar_spaceColor, colorSecondary)
                cornerRadiusDp = getDimension(R.styleable.RelationBar_cornerRadius, dpToPx(8f).toFloat())
            }
            progress = percentText.toInt()
            setPadding(0, 0, 0, 0)
        }

        updateView()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val fixedHeight = dpToPx(74f)
        val newHeightSpec = MeasureSpec.makeMeasureSpec(fixedHeight, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, newHeightSpec)
    }


    private fun setupView() {
        val textLayout = LinearLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            orientation = HORIZONTAL
        }

        titleTextView = TextView(
            ContextThemeWrapper(context, R.style.Theme_We_vote),
            null,
            0
        ).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            val titleText = getTitleTextFormat(titleText)
            text = titleText
        }

        percentTextView = TextView(
            ContextThemeWrapper(context, R.style.Theme_We_vote),
            null,
            0
        ).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            text = percentText
        }

        val percentSymbolTextView = TextView(
            ContextThemeWrapper(context, R.style.Theme_We_vote),
            null,
            0
        ).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            text = "%"
        }

        textLayout.addView(titleTextView)
        textLayout.addView(percentTextView)
        textLayout.addView(percentSymbolTextView)

        val progressLayout = LinearLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            orientation = HORIZONTAL
            foreground = ContextCompat.getDrawable(context, R.drawable.transparent_frame)
        }

        emptyView = View(context).apply {
            layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 100f)
            background = createFullRoundDrawable(spaceColor)
        }

        progressView = View(context).apply {
            layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT, 0f)
            background = createRoundedDrawable(progressColor, isLeftSide = false)
        }

        progressLayout.addView(progressView)
        progressLayout.addView(emptyView)

        addView(textLayout)
        addView(progressLayout)
    }

    private fun updateView() {
        val progressParams = progressView.layoutParams as LayoutParams
        progressParams.weight = progress.toFloat()

        val emptyParams = emptyView.layoutParams as LayoutParams
        emptyParams.weight = (100 - progress).toFloat()

        progressView.layoutParams = progressParams
        emptyView.layoutParams = emptyParams

        percentTextView.text = percentText
        titleTextView.text = getTitleTextFormat(titleText)

        if (progress == 100 || progress == 0) {
            progressView.background = createFullRoundDrawable(progressColor)
            emptyView.background = createFullRoundDrawable(spaceColor)
        } else {
            progressView.background = createRoundedDrawable(progressColor, isLeftSide = true)
            emptyView.background = createRoundedDrawable(spaceColor, isLeftSide = false)
        }

        requestLayout()
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

    private fun getTitleTextFormat(text: String): String {
        return "$text: "
    }
}
