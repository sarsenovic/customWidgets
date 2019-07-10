package com.example.testcustomwidgetslibrary;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

public class Res extends Resources {
    private String color;

    /**
     * @param assets
     * @param metrics
     * @param config
     * @deprecated
     */
    public Res(AssetManager assets, DisplayMetrics metrics, Configuration config, String color) {
        super(assets, metrics, config);
        this.color = color;
    }

    public Res(Resources original) {
        super(original.getAssets(), original.getDisplayMetrics(), original.getConfiguration());
    }



    @Override
    public int getColor(int id, @Nullable Theme theme) throws NotFoundException {
        switch (getResourceEntryName(id)) {
            case "loading_dialog_color":
                return Color.parseColor(color);
            default:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return super.getColor(id, theme);
                }
                return super.getColor(id);

        }
    }
}
