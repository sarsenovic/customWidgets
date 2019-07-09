package com.sasaarsenovic.customwidgetslibrary;

public interface RequestUploadListener {
    void onRequestUploadSuccessful(Object jsonObject, String requestIndicator);
    void onRequestUploadFailed(Object objectError, String requestIndicator);
}
