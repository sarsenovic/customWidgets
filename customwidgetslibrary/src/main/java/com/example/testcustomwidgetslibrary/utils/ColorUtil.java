package com.example.testcustomwidgetslibrary.utils;

import android.graphics.Color;

public class ColorUtil {

    public static int getDarkerColor(int color, double multiplier) {
        //veci multiplier daje tamniju boju
        int redColor = Color.red(color);
        int greenColor = Color.green(color);
        int blueColor = Color.blue(color);

        redColor = darkerColor(redColor, multiplier);
        greenColor = darkerColor(greenColor, multiplier);
        blueColor = darkerColor(blueColor, multiplier);
        int alpha = Color.alpha(color);

        return Color.argb(alpha, redColor, greenColor, blueColor);
    }

    public static int getLighterColor(int color, double multiplier) {
        //veci multiplier daje svetliju boju
        int redColor = Color.red(color);
        int greenColor = Color.green(color);
        int blueColor = Color.blue(color);

        redColor = lighterColor(redColor, multiplier);
        greenColor = lighterColor(greenColor, multiplier);
        blueColor = lighterColor(blueColor, multiplier);
        int alpha = Color.alpha(color);

        return Color.argb(alpha, redColor, greenColor, blueColor);
    }

    private static int darkerColor(int color, double multiplier) {
        return (int) Math.max(color - (color * multiplier), 0);
    }

    private static int lighterColor(int color, double multiplier) {
            return (int) Math.min(color + (color * multiplier), 255);
    }
}
