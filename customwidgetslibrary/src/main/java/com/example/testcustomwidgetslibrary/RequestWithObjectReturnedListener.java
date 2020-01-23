package com.example.testcustomwidgetslibrary;

public interface RequestWithObjectReturnedListener {
    void onRequestWithObjectLoadSuccessful(Object jsonObject, String requestIndicator, Object model);
    void onRequestWithObjectLoadFailed(Object objectError, String requestIndicator, Object model);
}
