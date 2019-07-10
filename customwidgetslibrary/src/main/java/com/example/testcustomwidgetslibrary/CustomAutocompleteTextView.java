package com.example.testcustomwidgetslibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

@SuppressLint("AppCompatCustomView")
public class CustomAutocompleteTextView extends AutoCompleteTextView {

    public CustomAutocompleteTextView(Context context) {
        super(context);
    }

    public CustomAutocompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypography(attrs);
    }

    public CustomAutocompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypography(attrs);
    }

    private void setTypography(AttributeSet attributeSet) {
        LoaderForFonts.setTypography(this, attributeSet);
    }
}
