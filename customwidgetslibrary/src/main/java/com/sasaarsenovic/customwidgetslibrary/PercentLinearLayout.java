package com.sasaarsenovic.customwidgetslibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.LinearLayout;

import static android.content.Context.WINDOW_SERVICE;

public class PercentLinearLayout extends LinearLayout {
    private float widthPercent;
    private float heightPercent;
    private int screenWidth;
    private int screenHeight;

    public PercentLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PercentLinearLayout,
                0, 0);


        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
            screenHeight = size.y;
        }

        try {
            widthPercent = typedArray.getFloat(R.styleable.PercentLinearLayout_widthPercent, 0);
            heightPercent = typedArray.getFloat(R.styleable.PercentLinearLayout_heightPercent, 0);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width, height;

        if (widthPercent > 0 && screenWidth > 0)
            width = (int)(screenWidth * widthPercent);
        else
            width = getMeasuredWidth();

        if (heightPercent > 0 && screenHeight > 0)
            height = (int)(screenHeight * heightPercent);
        else
            height = getMeasuredHeight();

        this.setMeasuredDimension(width, height);
    }
}
