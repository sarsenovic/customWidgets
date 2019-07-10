package com.example.testcustomwidgetslibrary;


public interface RequestListener {
    void onRequestLoadSuccessful(Object jsonObject, String requestIndicator);
    void onRequestLoadFailed(Object objectError, String requestIndicator);
}
