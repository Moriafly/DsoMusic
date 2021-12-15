package com.dirror.music.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.dirror.music.R

@Composable
fun RoundedColumn(
     content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp, 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(id = R.color.colorPageBackground))
    ) {
        content()
    }
}