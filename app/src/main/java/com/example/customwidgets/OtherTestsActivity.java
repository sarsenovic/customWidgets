package com.example.customwidgets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testcustomwidgetslibrary.CustomEditText;

public class OtherTestsActivity extends AppCompatActivity implements CustomEditText.CustomEditTextListener {
    private CustomEditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_test);

        editText = findViewById(R.id.edit_text_test);
//        editText.setCallbackOnEditorActionListener(this);
//        editText.clearFocusAndCursor(EditorInfo.IME_ACTION_SEND, InputType.TYPE_CLASS_TEXT, false);
//        editText.allowContentScrolling();
    }

//    @Override
//    public void onEditorActionListener(TextView textView, int actionId, KeyEvent event) {
//        switch (actionId) {
//            case EditorInfo.IME_ACTION_SEND:
//                Toast.makeText(this, "GOOD", Toast.LENGTH_SHORT).show();
//                break;
//        }
//    }

    @Override
    public void onEditorActionListener(TextView textView, int actionId, KeyEvent event, EditText editText, String tag, Object object) {

    }
}
