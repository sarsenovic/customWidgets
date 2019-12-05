package com.example.testcustomwidgetslibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.example.testcustomwidgetslibrary.R;

public class CalculationsLinearLayout {
    private String linearLayoutAspectRatio;
    private String linearLayoutFixedParam;
    private int linearLayoutAspectWidth;
    private int linearLayoutAspectHeight;

    public void checkScalingType(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                    attributeSet,
                    R.styleable.CustomLinearLayout,
                    0, 0);

            try {
                linearLayoutAspectRatio = typedArray.getString(R.styleable.CustomLinearLayout_linearLayoutAspectRatio);
                linearLayoutFixedParam = typedArray.getString(R.styleable.CustomLinearLayout_linearLayoutFixedParam);
                linearLayoutAspectWidth = typedArray.getInt(R.styleable.CustomLinearLayout_linearLayoutAspectWidth, -1);
                linearLayoutAspectHeight = typedArray.getInt(R.styleable.CustomLinearLayout_linearLayoutAspectHeight, -1);
//                customHorizontalRatio = typedArray.getInt(R.styleable.CustomImageView_customHorizontalRatio, 0);
//                customVerticalRatio = typedArray.getInt(R.styleable.CustomImageView_customVerticalRatio, 0);
            } finally {
                typedArray.recycle();
            }
        }
    }

    public int ratioCalculation(String aspectRatio, int parameterForEdit, int customWidth, int customHeight) {
        if (aspectRatio != null) {
            if (aspectRatio.equals("43") || aspectRatio.equals("4:3")) {
                return (3 * parameterForEdit) / 4;
            } else if (aspectRatio.equals("169") || aspectRatio.equals("16:9")) {
                return (9 * parameterForEdit) / 16;
            } else if (aspectRatio.equals("21") || aspectRatio.equals("2:1")) {
                return parameterForEdit / 2;
            } else if (aspectRatio.equals("3129") || aspectRatio.equals("31:29")) {
                return (29 * parameterForEdit) / 31;
            }
        } else {
            return (customWidth * parameterForEdit) / customHeight;
        }
        return 0;
    }

    public String getLinearLayoutAspectRatio() {
        return linearLayoutAspectRatio;
    }

    public String getLinearLayoutFixedParam() {
        return linearLayoutFixedParam;
    }

    public int getLinearLayoutAspectWidth() {
        return linearLayoutAspectWidth;
    }

    public int getLinearLayoutAspectHeight() {
        return linearLayoutAspectHeight;
    }

    public static float getDpFromPx(Context context, float px) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float getPxFromDp(Context context, float dp) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
