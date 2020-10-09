package com.dirror.music.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.dirror.music.R
import kotlinx.android.synthetic.main.dirrorx_value_view_layout.view.*

class ValueView(context: Context, attrs: AttributeSet): androidx.constraintlayout.widget.ConstraintLayout(context, attrs){
    private val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ValueView)
    private val title = typedArray.getString(R.styleable.ValueView_text)
    private val value = typedArray.getString(R.styleable.ValueView_value)

    init {
        LayoutInflater.from(context).inflate(R.layout.dirrorx_value_view_layout, this)
        tvTitle.text = title
        tvValue.text = value
    }

    fun setTitle(string: String) {
        tvTitle.text = string
    }

    fun setValue(string: String) {
        tvValue.text = string
    }

}