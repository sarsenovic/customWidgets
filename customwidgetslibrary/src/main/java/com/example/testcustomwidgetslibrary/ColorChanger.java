package com.example.testcustomwidgetslibrary;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;

class ColorChanger {
    private Context context;
    private Integer color = null;
    private Drawable drawable = null;

    public ColorChanger(Context context) {
        this.context = context;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(@ColorInt Integer color) {
        this.color = color;
    }

    public void setColorResourceId(@ColorRes Integer colorResourceId) {
        if (colorResourceId != null) {
            this.color = ResourcesCompat.getColor(context.getResources(), colorResourceId, null);
        }
    }

    public Drawable changeColorById(@DrawableRes Integer drawableResId, @ColorRes Integer colorResId) {
        setDrawable(drawable);
        if (colorResId != null)
            setColorResId(colorResId);
        return getColorChangedDrawable();
    }

    public Drawable changeColorByColor(@DrawableRes Integer drawableResId, @ColorInt Integer color) {
        setDrawable(drawableResId);
        if (color != null)
            setColor(color);
        return getColorChangedDrawable();
    }

    private void changeColor() {
        if (drawable == null) {
            throw new NullPointerException("Drawable is null. Please set drawable by setDrawable() or setBitmap() method");
        }
        if (color == null) {
            throw new NullPointerException("Color is null. Please set color by setColor() or setColorResID() method");
        }
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
    }

    public Drawable getColorChangedDrawable() {
        changeColor();
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public void setDrawable(@DrawableRes int drawableResId) {
        this.drawable = ResourcesCompat.getDrawable(context.getResources(), drawableResId, null);
    }

    public void setColorResId(@ColorRes Integer colorResId) {
        if (colorResId != null) {
            this.color = ResourcesCompat.getColor(context.getResources(), colorResId, null);
        }
    }
}
