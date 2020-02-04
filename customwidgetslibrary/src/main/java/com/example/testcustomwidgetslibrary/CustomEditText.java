package com.example.testcustomwidgetslibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class CustomEditText extends EditText {
    private CustomEditTextListener callbackOnEditorActionListener;
    private boolean canDoSomething = true;

    private boolean capsWords = false;
    private boolean capsSentences = false;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypography(attrs);
        attributesWorks(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypography(attrs);
        attributesWorks(context, attrs);
    }

    private void attributesWorks(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomEditText, 0, 0);
            try {
                capsWords = typedArray.getBoolean(R.styleable.CustomEditText_caps_words, false);
                capsSentences = typedArray.getBoolean(R.styleable.CustomEditText_caps_sentences, false);

                if (capsWords)
                    this.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                if (capsSentences)
                    this.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            } finally {
                typedArray.recycle();
            }
        }
    }

    public boolean isCapsWords() {
        return capsWords;
    }

    public void setCapsWords(boolean capsWords) {
        this.capsWords = capsWords;
        if (capsWords)
            this.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_FLAG_CAP_WORDS);
    }

    public boolean isCapsSentences() {
        return capsSentences;
    }

    public void setCapsSentences(boolean capsSentences) {
        this.capsSentences = capsSentences;
        if (capsSentences)
            this.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    }

    private void setTypography(AttributeSet attributeSet) {
        LoaderForFonts.setTypography(this, attributeSet);
    }

    public CustomEditTextListener getCallbackOnEditorActionListener() {
        return callbackOnEditorActionListener;
    }

    public void setCallbackOnEditorActionListener(CustomEditTextListener callbackOnEditorActionListener) {
        this.callbackOnEditorActionListener = callbackOnEditorActionListener;
    }

    //    @Override
//    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
//        super.onFocusChanged(focused, direction, previouslyFocusedRect);
//        if (focused)
//            setCursorVisible(true);
//        else
//            setCursorVisible(false);
//    }

    /**
     * This function clear focus and cursor on action which is added through function attributes.
     *
     * @param editorInfoImeOptions               EditorInfo options (ex. EditorInfo.IME_ACTION_DONE)
     * @param rawInputType                       Raw input type (ex. InputType.TYPE_CLASS_TEXT)
     * @param returnTypeOfOnEditorActionListener boolean which will be returned from setOnEditorActionListener
     * @param tag                                String tag for callback recognize which object is in use
     * @param object                             Object which will be returned in callback function
     */
    public void clearFocusAndCursor(final CustomEditTextListener callback, final int editorInfoImeOptions, int rawInputType, final boolean returnTypeOfOnEditorActionListener, final String tag, final Object object) {
        this.setImeOptions(editorInfoImeOptions);
        this.setRawInputType(rawInputType);

        this.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                setCursorVisible(false);
                clearFocus();
                hideKeyboard();
                canDoSomething = false;
                if (callback != null)
                    callback.onEditorActionListener(v, actionId, event, CustomEditText.this, tag, object);

                return returnTypeOfOnEditorActionListener;
            }
        });

        this.setOnClickListener((new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCursorVisible(true);
                if (canDoSomething) {
                    requestFocus();
                } else {
                    canDoSomething = true;
                }
            }
        }));
    }

    private void hideKeyboard() {
        if (getContext() != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
        }
    }

    private void showKeyboard() {
        if (getContext() != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    /**
     * This function allows editText content scrolling.
     */
    public void allowContentScrolling() {
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }

    public interface CustomEditTextListener {
        void onEditorActionListener(TextView textView, int actionId, KeyEvent event, EditText editText, String tag, Object object);
    }
}
