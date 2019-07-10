package com.example.testcustomwidgetslibrary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Builds an alert dialog with custom features.
 */
public class DialogMessage {
    private Context context;
    private String title;
    private String message;
    private boolean cancelable;

    /**
     * Constructor
     *
     * @param context Context.
     * @param title not null String, title in dialog.
     * @param message not null String message in dialog.
     */
    public DialogMessage(Context context, String title, String message, boolean cancelable) {
        this.context = context;
        this.title = title;
        this.message = message;
        this.cancelable = cancelable;
    }

    /**
     * Builds an alert dialog just with message.
     */
    public void showDialogMessage() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title)
                .setMessage(message)
                .setCancelable(cancelable)
                .create()
                .show();
    }

    /**
     * Builds an alert dialog with message and default button.
     *
     * @param actionName not null String for name of the button (action)
     */
    public void showDialogMessageWithDefaultAction(String actionName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(actionName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Default
                    }
                })
                .setCancelable(cancelable)
                .create()
                .show();
    }

    /**
     * Builds an alert dialog with one action.
     *
     * @param callback Interface for actions
     * @param positiveButtonText not null String for name of the button (action)
     * @param tag not null String for easier way to control alert dialogs if there is more than one in class
     */
    public void showDialogMessageWithOneAction(final DialogMessageInterface callback, String positiveButtonText, final String tag) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onDialogClickPositiveButton(tag);
                    }
                })
                .setCancelable(cancelable)
                .create()
                .show();
    }

    /**
     * Builds an alert dialog with one action.
     *
     * @param callback Interface for actions
     * @param positiveButtonText not null String for name of the button 1 (action1)
     * @param negativeButtonText not null String for name of the button 2 (action2)
     * @param tag not null String for easier way to control alert dialogs if there is more than one in class
     */
    public void showDialogMessageWithTwoActions(final DialogMessageInterface callback, String positiveButtonText,
                                                       String negativeButtonText, final String tag) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onDialogClickPositiveButton(tag);
                    }
                })
                .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onDialogClickNegativeButton(tag);
                    }
                })
                .setCancelable(cancelable)
                .create()
                .show();
    }

    /**
     * Builds an alert dialog with one action.
     *
     * @param callback Interface for actions
     * @param positiveButtonText not null String for name of the button 1 (action1)
     * @param negativeButtonText not null String for name of the button 2 (action2)
     * @param neutralButtonText not null String for name of the button 3 (action3)
     * @param tag not null String for easier way to control alert dialogs if there is more than one in class
     */
    public void showDialogMessageWithThreeActions(final DialogMessageInterface callback, String positiveButtonText,
                                                         String negativeButtonText, String neutralButtonText, final String tag) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onDialogClickPositiveButton(tag);
                    }
                })
                .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onDialogClickNegativeButton(tag);
                    }
                })
                .setNeutralButton(neutralButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onDialogClickNeutralButton(tag);
                    }
                })
                .setCancelable(cancelable)
                .create()
                .show();
    }
}
