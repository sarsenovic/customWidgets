package com.example.testcustomwidgetslibrary;

import android.annotation.SuppressLint;
import android.content.Context;
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

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypography(attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypography(attrs);
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
     */
    public void clearFocusAndCursor(final int editorInfoImeOptions, int rawInputType, final boolean returnTypeOfOnEditorActionListener, final String tag) {
        this.setImeOptions(editorInfoImeOptions);
        this.setRawInputType(rawInputType);

        this.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                setCursorVisible(false);
                clearFocus();
                hideKeyboard();
                canDoSomething = false;
                if (callbackOnEditorActionListener != null)
                    callbackOnEditorActionListener.onEditorActionListener(v, actionId, event, CustomEditText.this, tag);

                return returnTypeOfOnEditorActionListener;
            }
        });

        this.setOnClickListener((new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canDoSomething) {
                    requestFocus();
                    setCursorVisible(true);
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
        void onEditorActionListener(TextView textView, int actionId, KeyEvent event, EditText editText, String tag);
    }
}
