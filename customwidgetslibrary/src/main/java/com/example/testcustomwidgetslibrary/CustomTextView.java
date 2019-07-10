package com.example.testcustomwidgetslibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;


@SuppressLint("AppCompatCustomView")
public class CustomTextView extends TextView {
    private String fontName = "";

    public CustomTextView(Context context) {
        super(context);
//        init(context, null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        init(context, attrs);
        setTypography(attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init(context, attrs);
        setTypography(attrs);
    }

    private void setTypography(AttributeSet attributeSet) {
        LoaderForFonts.setTypography(this, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
//        if (attributeSet != null) {
//            TypedArray typedArray = context.getTheme().obtainStyledAttributes(
//                    attributeSet,
//                    R.styleable.PercentLinearLayout,
//                    0, 0);
//
//            try {
//                fontName = typedArray.getString(R.styleable.CustomTextView_fontName);
//            } finally {
//                typedArray.recycle();
//            }
//        }

        Typeface typeface = null;
        File fontFile = null;
        AssetManager am = context.getAssets();

//        try {
//            String fileList[] = am.list("data");
//            if (fileList != null) {
//                for (String fileName : fileList) {
//                    if (fileName.equals(fontName)) {
//                        fontFile = new File(fileName);
//                        typeface = Typeface.createFromFile(fontFile);
//                        setTypeface(typeface);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        try {
//            fontFile = new File(new URI("file:///android_assets/ShadowsIntoLight.ttf"));
        String filePath = "file://ShadowsIntoLight.ttf";
        String test = Uri.parse("file:///android_assets/ShadowsIntoLight.ttf").toString();
//            fontFile = new File(new URI("file:///android_asset/"));

//        InputStream stream;
//        try {
//            stream = context.getAssets().open(test);
//        } catch (IOException e) {
//            throw new IllegalStateException("Unable to find file " + test, e);
//        }


//        String pathname;
//        fontFile = new File(filePath);
//        if (fontFile.exists()) {
//            typeface = Typeface.createFromFile(fontFile);
//            setTypeface(typeface);
//        } else {
//            ToastMessage.toaster(context, "File doesn't exist. Check your assets folder.");
//        }
//        }
//        catch (URISyntaxException e) {
//            e.printStackTrace();
//        }

//        String path = context.getResources().openRawResource()
//        InputStream is = new FileInputStream();
//        InputStream path = context.getResources().openRawResource(getResources().getIdentifier("shadows_into_light", "raw", context.getPackageName()));
//        File file = new File("/res/raw/shadows_into_light.ttf");
//        Log.e("TAG!!!!","path " + path);

        try {
            String[] files = am.list("/assets");

            for (int i = 0; i < files.length; i++) {
                Log.e("TAG!!!!","File " + i + ": " + files[i]);
            }
        } catch (IOException e) {
            ToastMessage.toaster(context, "Prsla lista asseta");
        }
    }
}
