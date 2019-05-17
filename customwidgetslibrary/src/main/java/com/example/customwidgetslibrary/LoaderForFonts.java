package com.example.customwidgetslibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by sarsenovic on 26/11/18.
 */
public class LoaderForFonts {
    private static String TAG = "LoaderForFonts";
    private static String SEPARATOR = "-";
    private static String FONT_ROOT = "fonts";

    private static LoaderForFonts mLoaderForFonts;
    private String[] mFontFiles;
    private String mDefaultFontName;
    private String mDefaultFontVariant;
    private HashMap<String, Typeface> mTypefaces;

    private static LoaderForFonts getInstance(Context context) {
        if (mLoaderForFonts == null) {
            mLoaderForFonts = new LoaderForFonts(context);
        } else {
            mLoaderForFonts.initFontFiles(context);
            mLoaderForFonts.getDefaultsFromContext(context);
        }
        return mLoaderForFonts;
    }

    private LoaderForFonts(Context context) {
        this();

        getDefaultsFromContext(context);
        initFontFiles(context);
    }

    private LoaderForFonts() {
        mTypefaces = new HashMap<>();
    }

    public static Typeface getTypeface(Context context, String fontName, String fontVariant) {
        LoaderForFonts loaderForFonts = getInstance(context);
        loaderForFonts.logd("getTypeface fontName: " + fontName + " fontVariant: " + fontVariant);
        if (TextUtils.isEmpty(fontName)) {
            fontName = loaderForFonts.mDefaultFontName;
        }
        if (TextUtils.isEmpty(fontVariant)) {
            fontVariant = loaderForFonts.mDefaultFontVariant;
        }

        String hash = getHash(fontName, fontVariant);
        if (loaderForFonts.mTypefaces.containsKey(hash)) {
            return loaderForFonts.mTypefaces.get(hash);
        } else {
            return loaderForFonts.getNewTypeface(context, fontName, fontVariant, hash);
        }
    }

    public static Typeface getTypeface(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        String fontName, fontVariant;
        try {
            fontName = a.getString(R.styleable.CustomTextView_font_name);
            fontVariant = a.getString(R.styleable.CustomTextView_font_variant);
        } finally {
            a.recycle();
        }

        return getTypeface(context, fontName, fontVariant);
    }

    public static void setTypeface(android.widget.TextView textView, AttributeSet attrs) {
        Typeface typeface = getTypeface(textView.getContext(), attrs);
        textView.setTypeface(typeface);
    }

    public static void setTypography(android.widget.TextView textView, AttributeSet attrs) {
        if (!textView.isInEditMode()) {
            setTypeface(textView, attrs);
        }
    }

    private static String getHash(String fontName, String fontVariant) {
        return fontName + SEPARATOR + fontVariant;
    }

    private Typeface getNewTypeface(Context context, String fontName, String fontVariant, String hash) {
        Typeface typeface = null;
        boolean fallback = false;

        String fontFile = getFontFile(fontName, fontVariant);
        if (TextUtils.isEmpty(fontFile) && !TextUtils.isEmpty(fontVariant)) {
            fallback = true;
            fontFile = getFontFile(fontName, null);
        }

        if (!TextUtils.isEmpty(fontVariant)) {
            fontFile = getFontFile(fontName, fontVariant);

            if (TextUtils.isEmpty(fontFile)) {
                fallback = true;
                fontFile = getFontFile(fontName, null);
                logd("falling back to base font " + fontFile);
            }
        }

        try {
            typeface = Typeface.createFromAsset(context.getAssets(), fontFile);
        } catch (RuntimeException e) {
            loge("Font file not found for " + fontName);
        }

        if (fallback) {
            typeface = getCorrectedTypeface(typeface, fontVariant);
        }

        mTypefaces.put(hash, typeface);
        return typeface;
    }

    private void initFontFiles(Context context) {
        if (mFontFiles == null) {
            try {
                mFontFiles = context.getAssets().list(FONT_ROOT);
                if (mFontFiles != null)
                    logd(mFontFiles.toString());
            } catch (IOException e) {
                loge("No fonts folder found in assets");
                e.printStackTrace();
            }
        }
    }


    private String getFontFile(String fontName, String fontVariant) {
        if (mFontFiles == null || mFontFiles.length == 0) {
            loge("No fonts folder in assets");
            return null;
        }

        if (TextUtils.isEmpty(fontName)) {
            loge("Default font not set");
            return null;
        }

        String fontFile = fontName;
        if (!TextUtils.isEmpty(fontVariant)) {
            fontFile = fontFile + SEPARATOR + fontVariant;
        }

        return searchInFontFiles(fontFile);
    }

    private String searchInFontFiles(String fontFile) {
        for (String file : mFontFiles) {
            if (getFileWithoutExt(file).toLowerCase().equals(fontFile.toLowerCase())) {
                return FONT_ROOT + File.separator + file;
            }
        }
        return null;
    }

    private Typeface getCorrectedTypeface(Typeface typeface, String fontVariant) {
        String lowerVariant = fontVariant.toLowerCase();

        if (lowerVariant.equals("bold")) {
            logd("No bold font found. Making it bold using code. Not advisable.");
            return Typeface.create(typeface, Typeface.BOLD);
        } else if (lowerVariant.equals("italic")) {
            logd("No italic font found. Making it italic using code. Not advisable.");
            return Typeface.create(typeface, Typeface.ITALIC);
        } else if (lowerVariant.equals("bolditalic")) {
            logd("No bolditalic font found. Making it bolditalic using code. Not advisable.");
            return Typeface.create(typeface, Typeface.BOLD_ITALIC);
        }

        loge("Font variant not recognized. Please add font for " + fontVariant);
        return typeface;
    }

    private String getFileWithoutExt(String file) {
        if (!TextUtils.isEmpty(file)) {
            int dotIndex = file.lastIndexOf(".");
            if (dotIndex != -1) {
                return file.substring(0, dotIndex);
            }
        }

        return file;
    }

    private void getDefaultsFromContext(Context context) {
        if (mDefaultFontName == null) {
            TypedValue fontNameValue = new TypedValue();
            TypedValue fontVariantValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.defaultFontName, fontNameValue, true);
            context.getTheme().resolveAttribute(R.attr.defaultFontVariant, fontVariantValue, true);
            String defaultFontName = (String) fontNameValue.string;
            String defaultFontVariant = (String) fontVariantValue.string;
            setDefaults(defaultFontName, defaultFontVariant);
        }
    }

    private void setDefaults(String fontName, String fontVariant) {
        setDefaultFontName(fontName);
        setDefaultFontVariant(fontVariant);
    }

    private void setDefaultFontName(String fontName) {
        mDefaultFontName = fontName;
    }

    private void setDefaultFontVariant(String fontVariant) {
        mDefaultFontVariant = fontVariant;
    }

    private void logd(String message) {
//        if (loggingEnabled) {
        Log.d(TAG, message);
//        }
    }

    private void loge(String message) {
        Log.e(TAG, message);
    }
}
