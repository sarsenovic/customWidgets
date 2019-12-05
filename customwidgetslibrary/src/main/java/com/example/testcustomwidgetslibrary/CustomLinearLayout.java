package com.example.testcustomwidgetslibrary;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.example.testcustomwidgetslibrary.CalculationsLinearLayout;

public class CustomLinearLayout extends LinearLayout {
    private CalculationsLinearLayout calculations = new CalculationsLinearLayout();

    public CustomLinearLayout(Context context) {
        super(context);
    }

    public CustomLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        calculations.checkScalingType(context, attrs);
    }

    public CustomLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        calculations.checkScalingType(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (calculations != null) {
            if (calculations.getLinearLayoutAspectRatio() != null) {
                if (calculations.getLinearLayoutFixedParam() != null && !calculations.getLinearLayoutFixedParam().equals("")) {
                    if (calculations.getLinearLayoutFixedParam().equals("0")) {
                        //width is fixed
//                        height = ratioCalculation(calculations.getAspectRatio(), width);
                        height = calculations.ratioCalculation(calculations.getLinearLayoutAspectRatio(), width, 0, 0);
                    } else if (calculations.getLinearLayoutFixedParam().equals("1")) {
                        //height is fixed
                        width = calculations.ratioCalculation(calculations.getLinearLayoutAspectRatio(), height, 0, 0);
                    }
                } else {
                    //Default, width is fixed
                    height = calculations.ratioCalculation(calculations.getLinearLayoutAspectRatio(), width, 0, 0);
                }
            } else if (calculations.getLinearLayoutAspectWidth() > -1 && calculations.getLinearLayoutAspectHeight() > -1) {
                if (calculations.getLinearLayoutFixedParam() != null && !calculations.getLinearLayoutFixedParam().equals("")) {
                    if (calculations.getLinearLayoutFixedParam().equals("0")) {
                        //width is fixed
                        height = calculations.ratioCalculation(null, width, calculations.getLinearLayoutAspectWidth(), calculations.getLinearLayoutAspectHeight());
                    } else if (calculations.getLinearLayoutFixedParam().equals("1")) {
                        //height is fixed
                        width = calculations.ratioCalculation(null, height, calculations.getLinearLayoutAspectWidth(), calculations.getLinearLayoutAspectHeight());
                    }
                } else {
                    //Default, width is fixed
                    height = calculations.ratioCalculation(null, width, calculations.getLinearLayoutAspectWidth(), calculations.getLinearLayoutAspectHeight());
                }
            }
        }

        setMeasuredDimension(width, height);
    }
}
