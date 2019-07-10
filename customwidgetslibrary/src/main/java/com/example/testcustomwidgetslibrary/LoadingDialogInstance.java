package com.example.testcustomwidgetslibrary;

public class LoadingDialogInstance {
    public static LoadingDialog init(String message, boolean dismissOnBackClick, int style, int loadingProgressStyle) {
        return new LoadingDialog(message, dismissOnBackClick, style, loadingProgressStyle);
    }

    public static LoadingDialog init(String message, boolean dismissOnBackClick, int style, int loadingProgressStyle, boolean cancelableOnTouchOutside, boolean cancelable) {
        return new LoadingDialog(message, dismissOnBackClick, style, loadingProgressStyle, cancelableOnTouchOutside, cancelable);
    }
}
