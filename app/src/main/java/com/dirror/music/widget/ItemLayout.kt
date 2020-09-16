package com.dirror.music.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.dirror.music.R
import kotlinx.android.synthetic.main.dirrorx_item_layout.view.*

class ItemLayout(context: Context, attrs: AttributeSet): androidx.constraintlayout.widget.ConstraintLayout(context, attrs) {
    private val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemLayout)
    private val text = typedArray.getString(R.styleable.ItemLayout_text)
    private val itemType = typedArray.getInt(R.styleable.ItemLayout_itemType, 1)
    init {
        LayoutInflater.from(context).inflate(R.layout.dirrorx_item_layout, this)
        tvItem.text = text

        when (itemType) {
            // no
            0 -> ivGoto.visibility = View.INVISIBLE
            // goto
            1 -> ivGoto.visibility = View.VISIBLE
        }
    }

}