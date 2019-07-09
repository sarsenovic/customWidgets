package com.sasaarsenovic.customwidgetslibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class CustomImageView extends ImageView {
    private Calculations calculations = new Calculations();

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        calculations.checkScalingType(context, attrs);
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        calculations.checkScalingType(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (calculations != null) {
            if (calculations.getAspectRatio() != null) {
                if (calculations.getFixedParam() != null && !calculations.getFixedParam().equals("")) {
                    if (calculations.getFixedParam().equals("0")) {
                        //width is fixed
//                        height = ratioCalculation(calculations.getAspectRatio(), width);
                        height = calculations.ratioCalculation(calculations.getAspectRatio(), width);
                    } else if (calculations.getFixedParam().equals("1")) {
                        //height is fixed
                        width = calculations.ratioCalculation(calculations.getAspectRatio(), height);
                    }
                } else {
                    //Default, width is fixed
                    height = calculations.ratioCalculation(calculations.getAspectRatio(), width);
                }
            }
        }

        setMeasuredDimension(width, height);
    }
}
