package com.example.testcustomwidgetslibrary;

import android.content.Context;
import android.widget.Toast;

public class ToastMessage {

    public static void toaster(Context c, String message) {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

}
