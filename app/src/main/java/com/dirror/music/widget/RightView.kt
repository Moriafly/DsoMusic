package com.dirror.music.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dirror.music.R

class RightView(context: Context, attrs: AttributeSet): androidx.constraintlayout.widget.ConstraintLayout(context, attrs){
    private val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RightView)
    private val title = typedArray.getString(R.styleable.RightView_text)

    private var tvTitle: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.dirrorx_right_view_layout, this)
        tvTitle = findViewById(R.id.tvTitle)
        tvTitle.text = title
    }

    fun setTitle(string: String) {
        tvTitle.text = string
    }

    fun setRight() {
        val ivRight = findViewById<ImageView>(R.id.tvRight)
        ivRight.visibility = View.VISIBLE
    }

}