package com.dirror.music.ui.wheelView.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dirror.music.R;
import com.dirror.music.ui.wheelView.IPickerViewOperation;
import com.dirror.music.ui.wheelView.util.ScreenUtil;


public class ScrollPickerView extends RecyclerView {
    private Runnable mSmoothScrollTask;
    private Paint mBgPaint;
    private int mItemHeight;
    private int mItemWidth;
    private int mInitialY;
    private int mFirstLineY;
    private int mSecondLineY;
    private boolean mFirstAmend;

    public ScrollPickerView(@NonNull Context context) {
        this(context, null);
    }

    public ScrollPickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollPickerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initTask();
    }

    public void doDraw(Canvas canvas) {
        if (mItemHeight > 0) {
            int screenX = getWidth();
            int startX = screenX / 2 - mItemWidth / 2 - ScreenUtil.dpToPx(5);
            int stopX = mItemWidth + startX + ScreenUtil.dpToPx(5);

            canvas.drawLine(startX, mFirstLineY, stopX, mFirstLineY, mBgPaint);
            canvas.drawLine(startX, mSecondLineY, stopX, mSecondLineY, mBgPaint);
        }
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        doDraw(c);
        if (!mFirstAmend) {
            mFirstAmend = true;
            ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(getItemSelectedOffset(), 0);
        }
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        freshItemView();
    }

    private void initPaint() {
        if (mBgPaint == null) {
            mBgPaint = new Paint();
            mBgPaint.setColor(getLineColor());
            mBgPaint.setStrokeWidth(ScreenUtil.dpToPx(1f));
        }
    }


    private int getScrollYDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) this.getLayoutManager();
        if (layoutManager == null) {
            return 0;
        }
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisibleChildView = layoutManager.findViewByPosition(position);
        if (firstVisibleChildView == null) {
            return 0;
        }
        int itemHeight = firstVisibleChildView.getHeight();
        return (position) * itemHeight - firstVisibleChildView.getTop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            processItemOffset();
        }
        return super.onTouchEvent(e);
    }


    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {

        widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

        super.onMeasure(widthSpec, heightSpec);

        measureSize();

        setMeasuredDimension(mItemWidth, mItemHeight * getVisibleItemNumber());
    }

    private void measureSize() {
        if (getChildCount() > 0) {
            if (mItemHeight == 0) {
                mItemHeight = getChildAt(0).getMeasuredHeight();
            }
            if (mItemWidth == 0) {
                mItemWidth = getChildAt(0).getMeasuredWidth();
            }

            if (mFirstLineY == 0 || mSecondLineY == 0) {
                mFirstLineY = mItemHeight * getItemSelectedOffset();
                mSecondLineY = mItemHeight * (getItemSelectedOffset() + 1);
            }
        }
    }

    private void processItemOffset() {
        mInitialY = getScrollYDistance();
        postDelayed(mSmoothScrollTask, 30);
    }

    private void initTask() {
        mSmoothScrollTask = new Runnable() {
            @Override
            public void run() {
                int newY = getScrollYDistance();
                if (mInitialY != newY) {
                    mInitialY = getScrollYDistance();
                    postDelayed(mSmoothScrollTask, 30);
                } else if (mItemHeight > 0) {
                    final int offset = mInitialY % mItemHeight;//离选中区域中心的偏移量
                    if (offset == 0) {
                        return;
                    }
                    if (offset >= mItemHeight / 2) {//滚动区域超过了item高度的1/2，调整position的值
                        smoothScrollBy(0, mItemHeight - offset);
                    } else if (offset < mItemHeight / 2) {
                        smoothScrollBy(0, -offset);
                    }
                }
            }
        };
    }


    private int getVisibleItemNumber() {
        IPickerViewOperation operation = (IPickerViewOperation) getAdapter();
        if (operation != null) {
            return operation.getVisibleItemNumber();
        }
        return 3;
    }

    private int getItemSelectedOffset() {
        IPickerViewOperation operation = (IPickerViewOperation) getAdapter();
        if (operation != null) {
            return operation.getSelectedItemOffset();
        }
        return 1;
    }

    private int getLineColor() {
        IPickerViewOperation operation = (IPickerViewOperation) getAdapter();
        if (operation != null && operation.getLineColor() != 0) {
            return operation.getLineColor();
        }
        return getResources().getColor(R.color.colorPrimary);
    }

    private void updateView(View itemView, boolean isSelected) {
        IPickerViewOperation operation = (IPickerViewOperation) getAdapter();
        if (operation != null) {
            operation.updateView(itemView, isSelected);
        }
    }

    private void freshItemView() {
        for (int i = 0; i < getChildCount(); i++) {
            float itemViewY = getChildAt(i).getTop() + mItemHeight / 2;
            updateView(getChildAt(i), mFirstLineY < itemViewY && itemViewY < mSecondLineY);
        }
    }

    public int getCurrentItem(){
        LinearLayoutManager layoutManager = (LinearLayoutManager) this.getLayoutManager();
        return layoutManager.findFirstVisibleItemPosition();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initPaint();
    }
}
