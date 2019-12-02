package com.example.testcustomwidgetslibrary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.androidnetworking.AndroidNetworking;
import com.example.testcustomwidgetslibrary.R;

public class LoadingDialog {
    private ProgressDialog dialog;
    private String message;
    private boolean cancelable;
    private boolean cancelableOnTouchOutside;
    private boolean dismissOnBackClick;
    private int style;
    private short tag;

    /**
     * 0 for STYLE_SPINNER
     * 1 for STYLE_HORIZONTAL
     */
    private int loadingProgressStyle;
    private LoadingDialogProgressStyleEnum loadingProgressStyleEnum;

    public LoadingDialog() {
    }

    public LoadingDialog(Context context, String message, boolean dismissOnBackClick) {
        this.message = message;
        this.dismissOnBackClick = dismissOnBackClick;
        show(context);
    }

    public LoadingDialog(Context context, String message, boolean dismissOnBackClick, int style, int loadingProgressStyle) {
        this.message = message;
        this.dismissOnBackClick = dismissOnBackClick;
        this.style = style;
        this.loadingProgressStyle = loadingProgressStyle;
        show(context);
    }

    /**
     * If you have android os version below LOLLIPOP, you ought to add this line in your dialog style
     * <item name="android:windowBackground">@color/transparent</item>
     */


    public LoadingDialog(String message, boolean dismissOnBackClick, int style, int loadingProgressStyle) {
        this.message = message;
        this.dismissOnBackClick = dismissOnBackClick;
        this.style = style;
        this.loadingProgressStyle = loadingProgressStyle;
    }

    public LoadingDialog(String message, boolean dismissOnBackClick, int style, int loadingProgressStyle, boolean cancelableOnTouchOutside, boolean cancelable) {
        this.message = message;
        this.dismissOnBackClick = dismissOnBackClick;
        this.style = style;
        this.loadingProgressStyle = loadingProgressStyle;
        this.cancelable = cancelable;
        this.cancelableOnTouchOutside = cancelableOnTouchOutside;
    }

    public LoadingDialog(String message, boolean dismissOnBackClick, int style, LoadingDialogProgressStyleEnum loadingProgressStyleEnum, boolean cancelableOnTouchOutside, boolean cancelable) {
        this.message = message;
        this.dismissOnBackClick = dismissOnBackClick;
        this.style = style;
        this.loadingProgressStyleEnum = loadingProgressStyleEnum;
        this.cancelable = cancelable;
        this.cancelableOnTouchOutside = cancelableOnTouchOutside;
    }

    public void show(Context context) {
        if (style == 0) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                dialog = new ProgressDialog(context, R.style.LoadingDialogStyle);
            } else {
                dialog = new ProgressDialog(context, R.style.LoadingDialogStyle2);
            }
        } else {
            dialog = new ProgressDialog(context, style);
        }

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    if (dismissOnBackClick) {
                        AndroidNetworking.forceCancelAll();
                        dialog.dismiss();
                    }
                }
                return true;
            }
        });

        if (loadingProgressStyleEnum != null) {
            switch (loadingProgressStyleEnum) {
                case STYLE_SPINNER:
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    break;
                case STYLE_HORIZONTAL:
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    break;
                default:
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    break;

            }
        } else {
            if (loadingProgressStyle == 0) {
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            } else if (loadingProgressStyle == 1) {
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            } else {
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            }
        }

        if (message == null) {
            dialog.setMessage(context.getString(R.string.loading_dialog));
        } else {
            dialog.setMessage(message);
        }

        dialog.setIndeterminate(true);
//        dialog.setCanceledOnTouchOutside(cancelableOnTouchOutside);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(cancelable);
        dialog.show();
    }

    public void hide() {
        if (dialog != null) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                // U slučaju kada je dismiss pozvan asinhrono a activity prethodno ugašen
            }
        }
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public boolean isCancelableOnTouchOutside() {
        return cancelableOnTouchOutside;
    }

    public void setCancelableOnTouchOutside(boolean cancelableOnTouchOutside) {
        this.cancelableOnTouchOutside = cancelableOnTouchOutside;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDismissOnBackClick() {
        return dismissOnBackClick;
    }

    public void setDismissOnBackClick(boolean dismissOnBackClick) {
        this.dismissOnBackClick = dismissOnBackClick;
    }
}
