package com.dirror.music.ui.wheelView;


import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IViewProvider<T> {
    @LayoutRes
    int resLayout();

    void onBindView(@NonNull View view, @Nullable T itemData);

    void updateView(@NonNull View itemView, boolean isSelected);
}
