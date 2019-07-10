package com.example.testcustomwidgetslibrary;

public interface RequestUploadListener {
    void onRequestUploadSuccessful(Object jsonObject, String requestIndicator);
    void onRequestUploadFailed(Object objectError, String requestIndicator);
}
