package com.dirror.music.widget

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.dirror.music.R
import kotlinx.android.synthetic.main.dirrorx_titlebar_layout.view.*

class TitleBarLayout(context: Context, attrs: AttributeSet): androidx.constraintlayout.widget.ConstraintLayout(
    context,
    attrs
) {

    private val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleBarLayout)
    val text = typedArray.getString(R.styleable.TitleBarLayout_text)

    init {

        LayoutInflater.from(context).inflate(R.layout.dirrorx_titlebar_layout, this)

        tvTitleBar.text = text

        btnBack.setOnClickListener {
            val objectAnimator: ObjectAnimator = ObjectAnimator.ofFloat(it, "alpha", 0F, 1.0F)
            objectAnimator.duration = 1000
            objectAnimator.start()

            val activity = context as Activity
            activity.finish()
        }
    }

}