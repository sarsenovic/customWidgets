package com.example.testcustomwidgetslibrary;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.example.testcustomwidgetslibrary.exception.TagException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Builds a request with customizable params.
 */
public class Requests {
    private final String TAG = "RequestsTag";
    public static final String CONTENT_TYPE = "application/json";
    private Context context;
    private LoadingDialog loadingDialog;
    private LoadingDialog loadingDialogInstance;
    private OkHttpClient okHttpClient;
    private RequestListener requestListenerCallback;
    private RequestUploadListener multipartFileRequestListenerCallback;
    private ANRequest.PostRequestBuilder postRequestBuilder;
    private ANRequest.GetRequestBuilder getRequestBuilder;
    private ANRequest.MultiPartBuilder multiPartBuilder;
    private boolean loggingEnabled = false;
    private String loadingDialogMessage;
    private Map<String, Object> dialogsMap;

    public Requests(Context context, boolean loggingEnabled) {
        this.context = context;
        this.loggingEnabled = loggingEnabled;

        dialogsMap = new HashMap<>();
    }

//    public Requests(Context context, boolean loggingEnabled) {
//        this.context = context;
//        this.loggingEnabled = loggingEnabled;
//
//        loadingDialogInstance = LoadingDialogInstance.init("Loading, please wait...", false, 0, 0);
//    }

//    public Requests(Context context, boolean loggingEnabled, String loadingDialogMessage, int loadingProgressStyle) {
//        this.context = context;
//        this.loggingEnabled = loggingEnabled;
//        this.loadingDialogMessage = loadingDialogMessage;
//
//        loadingDialogInstance = LoadingDialogInstance.init(loadingDialogMessage, false, 0, loadingProgressStyle);
//    }
//
//    public Requests(Context context, boolean loggingEnabled, String loadingDialogMessage, int loadingDialogStyle, boolean dismissLoadingDialogOnBackClick, int loadingProgressStyle) {
//        this.context = context;
//        this.loggingEnabled = loggingEnabled;
//        this.loadingDialogMessage = loadingDialogMessage;
//
//        loadingDialogInstance = LoadingDialogInstance.init(loadingDialogMessage, dismissLoadingDialogOnBackClick, loadingDialogStyle, loadingProgressStyle);
//    }
//
//    public Requests(Context context, boolean loggingEnabled, String loadingDialogMessage, int loadingDialogStyle, boolean dismissLoadingDialogOnBackClick, int loadingProgressStyle,
//                    boolean cancelableOnTouchOutside, boolean cancelable) {
//        this.context = context;
//        this.loggingEnabled = loggingEnabled;
//        this.loadingDialogMessage = loadingDialogMessage;
//
//        loadingDialogInstance = LoadingDialogInstance.init(loadingDialogMessage, dismissLoadingDialogOnBackClick, loadingDialogStyle, loadingProgressStyle,
//                cancelableOnTouchOutside, cancelable);
//    }

    /**
     * Builds a post request.
     *
     * @param urlString              Url koji gadjamo.
     * @param bodyParams             Mapa<String, Object> parametara koji se salju.
     * @param typeOfExpectedResponse Naziv ocekivanog tipa response-a (Moguci su jsonObject (jsonobject, object), jsonArray (jsonarray, array), String (string)).
     * @param sendAsJSON             boolean koji je true ako podatke saljemo kao jsonObject (U ovom slucaju se bodyParams automatski konvertuju u jsonObject).
     * @param postCallback           Interface za uspesno i neuspesno izvrsavanje request-a.
     * @param showLoadingDialog      boolean koji je true ako zelimo da prikazemo loading dialog.
     * @param contentTypeString      String koji predstavlja CONTENT_TYPE u request-u (Ako se ostavi prazan ovaj parametar CONTENT_TYPE se u tom slucaju ne salje).
     * @param headerParamsMap        Mapa<String, String> parametara koji se salju u header-u request-a.
     * @param tag                    String koji svaki request obelezava razlicitim imenom. (Za slucaj ako u oviru jedne klase postoji vise postRequest metoda pa njima se moze pristupiti preko ovog indikatora).
     */
    public void createPostRequest(String urlString, Map<String, Object> bodyParams, String typeOfExpectedResponse, boolean sendAsJSON,
                                  final RequestListener postCallback, final boolean showLoadingDialog, String contentTypeString, Map<String, String> headerParamsMap, final String tag) {
        if (context != null) {
            if (urlString != null) {

                this.requestListenerCallback = postCallback;

//                if (showLoadingDialog)
//                    showLoadingDialog(context);

                if (showLoadingDialog) {
                    LoadingDialog loadingDialog = new LoadingDialog(loadingDialogMessage, false, 0, 0, false, false);
                    if (tag != null && !tag.equals("")) {
                        for (Map.Entry<String, Object> entry : dialogsMap.entrySet()) {
                            if (tag.equalsIgnoreCase(entry.getKey())) {
                                loge("Tag already exist!, Change tag and try again.");
                                return;
                            }
                        }
                        dialogsMap.put(tag, loadingDialog);
                        showLoadingDialog(context, loadingDialog, tag);
                    }
                }

                postRequestBuilder = AndroidNetworking.post(urlString)
                        .setOkHttpClient(getOkHttpClient());

                if (contentTypeString != null && !contentTypeString.equals(""))
                    postRequestBuilder.setContentType(contentTypeString);

                if (headerParamsMap != null && headerParamsMap.size() > 0) {
                    for (Map.Entry<String, String> entry : headerParamsMap.entrySet()) {
                        postRequestBuilder.addHeaders(entry.getKey(), entry.getValue());
                    }
                }

                if (tag != null && !tag.equals(""))
                    postRequestBuilder.setTag(tag);

                Map<String, Object> mainMap = new HashMap<>();

                if (bodyParams != null)
                    mainMap.putAll(bodyParams);

                if (sendAsJSON) {
                    JSONObject jsonObject = new JSONObject(mainMap);

                    try {
                        for (Map.Entry<String, Object> entry : mainMap.entrySet()) {
                            jsonObject.put(entry.getKey(), entry.getValue());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    postRequestBuilder.addStringBody(String.valueOf(jsonObject));
                    logi("String body added");
                } else if (!mainMap.isEmpty()) {
                    for (Map.Entry<String, Object> entry : mainMap.entrySet()) {
                        postRequestBuilder.addBodyParameter(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                    logi("Parameters added");
                }

                if (typeOfExpectedResponse != null && !typeOfExpectedResponse.equals("")) {
                    switch (typeOfExpectedResponse) {

                        case "jsonObject":
                        case "jsonobject":
                        case "json":
                        case "object":

                            postRequestBuilder.build().getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }
                                    if (response != null) {
                                        logi("Successful response");
                                        postCallback.onRequestLoadSuccessful(response, tag);
                                    }

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }

                                @Override
                                public void onError(ANError anError) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }
                                    postCallback.onRequestLoadFailed(anError, tag);
                                    loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }
                            });
                            break;

                        case "jsonArray":
                        case "jsonarray":
                        case "array":

                            postRequestBuilder.build().getAsJSONArray(new JSONArrayRequestListener() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }
                                    if (response != null) {
                                        logi("Successful response");
                                        postCallback.onRequestLoadSuccessful(response, tag);
                                    }

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }

                                @Override
                                public void onError(ANError anError) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }
                                    postCallback.onRequestLoadFailed(anError, tag);
                                    loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }
                            });
                            break;

                        case "String":
                        case "string":

                            postRequestBuilder.build().getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }
                                    if (response != null) {
                                        logi("Successful response");
                                        postCallback.onRequestLoadSuccessful(response, tag);
                                    }

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }

                                @Override
                                public void onError(ANError anError) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }
                                    postCallback.onRequestLoadFailed(anError, tag);
                                    loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }
                            });
                            break;

                        default:
                            ToastMessage.toaster(context, "Invalid response type");
                            break;
                    }
                } else {
                    loge("PostRequest: Type of expected response is invalid");
                }
            } else {
                loge("PostRequest: URL is null");
            }
        } else {
            loge("PostRequest: Context is null");
        }
    }


    /**
     * Builds a post request.
     *
     * @param urlString                       Url koji gadjamo.
     * @param bodyParams                      Mapa<String, Object> parametara koji se salju.
     * @param typeOfExpectedResponse          Naziv ocekivanog tipa response-a (Moguci su jsonObject (jsonobject, object), jsonArray (jsonarray, array), String (string)).
     * @param sendAsJSON                      boolean koji je true ako podatke saljemo kao jsonObject (U ovom slucaju se bodyParams automatski konvertuju u jsonObject).
     * @param postCallback                    Interface za uspesno i neuspesno izvrsavanje request-a.
     * @param showLoadingDialog               boolean koji je true ako zelimo da prikazemo loading dialog.
     * @param loadingDialogMessage            Message which will be displayed in loading dialog
     * @param loadingDialogStyle              Style of loading dialog (0 is for default)
     * @param loadingDialogProgressStyle      0 for spinner, 1 for loading progress bar
     * @param dismissLoadingDialogOnBackClick true if you want to dismiss dialog with system back click
     * @param contentTypeString               String koji predstavlja CONTENT_TYPE u request-u (Ako se ostavi prazan ovaj parametar CONTENT_TYPE se u tom slucaju ne salje).
     * @param headerParamsMap                 Mapa<String, String> parametara koji se salju u header-u request-a.
     * @param tag                             String koji svaki request obelezava razlicitim imenom. (Za slucaj ako u oviru jedne klase postoji vise postRequest metoda pa njima se moze pristupiti preko ovog indikatora).
     */
    public void createPostRequest(String urlString, Map<String, Object> bodyParams, String typeOfExpectedResponse, boolean sendAsJSON,
                                  final RequestListener postCallback, final boolean showLoadingDialog, String loadingDialogMessage, int loadingDialogStyle, int loadingDialogProgressStyle,
                                  boolean dismissLoadingDialogOnBackClick, String contentTypeString, Map<String, String> headerParamsMap, final String tag) throws TagException {
        if (context != null) {
            if (urlString != null) {

                this.requestListenerCallback = postCallback;

                if (showLoadingDialog) {
                    LoadingDialog loadingDialog = new LoadingDialog(loadingDialogMessage, dismissLoadingDialogOnBackClick, loadingDialogStyle, loadingDialogProgressStyle, true, true);
                    if (tag != null && !tag.equals("")) {
                        for (Map.Entry<String, Object> entry : dialogsMap.entrySet()) {
                            if (tag.equalsIgnoreCase(entry.getKey())) {
                                loge("Tag already exist!");
                                throw new TagException("Tag already exist!");
                            }
                        }
                        dialogsMap.put(tag, loadingDialog);
                        showLoadingDialog(context, loadingDialog, tag);
                    }
                }

                postRequestBuilder = AndroidNetworking.post(urlString)
                        .setOkHttpClient(getOkHttpClient());

                if (contentTypeString != null && !contentTypeString.equals(""))
                    postRequestBuilder.setContentType(contentTypeString);

                if (headerParamsMap != null && headerParamsMap.size() > 0) {
                    for (Map.Entry<String, String> entry : headerParamsMap.entrySet()) {
                        postRequestBuilder.addHeaders(entry.getKey(), entry.getValue());
                    }
                }

                if (tag != null && !tag.equals(""))
                    postRequestBuilder.setTag(tag);

                Map<String, Object> mainMap = new HashMap<>();

                if (bodyParams != null)
                    mainMap.putAll(bodyParams);

                if (sendAsJSON) {
                    JSONObject jsonObject = new JSONObject(mainMap);

                    try {
                        for (Map.Entry<String, Object> entry : mainMap.entrySet()) {
                            jsonObject.put(entry.getKey(), entry.getValue());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    postRequestBuilder.addStringBody(String.valueOf(jsonObject));
                    logi("String body added");
                } else if (!mainMap.isEmpty()) {
                    for (Map.Entry<String, Object> entry : mainMap.entrySet()) {
                        postRequestBuilder.addBodyParameter(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                    logi("Parameters added");
                }

                if (typeOfExpectedResponse != null && !typeOfExpectedResponse.equals("")) {
                    switch (typeOfExpectedResponse) {

                        case "jsonObject":
                        case "jsonobject":
                        case "json":
                        case "object":

                            postRequestBuilder.build().getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }

                                    if (response != null) {
                                        logi("Successful response");
                                        postCallback.onRequestLoadSuccessful(response, tag);
                                    }

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }

                                @Override
                                public void onError(ANError anError) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }
                                    postCallback.onRequestLoadFailed(anError, tag);
                                    loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }
                            });
                            break;

                        case "jsonArray":
                        case "jsonarray":
                        case "array":

                            postRequestBuilder.build().getAsJSONArray(new JSONArrayRequestListener() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }
                                    if (response != null) {
                                        logi("Successful response");
                                        postCallback.onRequestLoadSuccessful(response, tag);
                                    }

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }

                                @Override
                                public void onError(ANError anError) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }
                                    postCallback.onRequestLoadFailed(anError, tag);
                                    loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }
                            });
                            break;

                        case "String":
                        case "string":

                            postRequestBuilder.build().getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }
                                    if (response != null) {
                                        logi("Successful response");
                                        postCallback.onRequestLoadSuccessful(response, tag);
                                    }

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }

                                @Override
                                public void onError(ANError anError) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }
                                    postCallback.onRequestLoadFailed(anError, tag);
                                    loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }
                            });
                            break;

                        default:
                            ToastMessage.toaster(context, "Invalid response type");
                            break;
                    }
                } else {
                    loge("PostRequest: Type of expected response is invalid");
                }
            } else {
                loge("PostRequest: URL is null");
            }
        } else {
            loge("PostRequest: Context is null");
        }
    }

    /**
     * Builds a post request.
     *
     * @param urlString                       Url koji gadjamo.
     * @param typeOfExpectedResponse          Naziv ocekivanog tipa response-a (Moguci su jsonObject (jsonobject, object), jsonArray (jsonarray, array), String (string)).
     * @param postCallback                    Interface za uspesno i neuspesno izvrsavanje request-a.
     * @param showLoadingDialog               boolean koji je true ako zelimo da prikazemo loading dialog.
     * @param loadingDialogMessage            Message which will be displayed in loading dialog
     * @param loadingDialogStyle              Style of loading dialog (0 is for default)
     * @param loadingDialogProgressStyle      0 for spinner, 1 for loading progress bar
     * @param dismissLoadingDialogOnBackClick true if you want to dismiss dialog with system back click
     * @param contentTypeString               String koji predstavlja CONTENT_TYPE u request-u (Ako se ostavi prazan ovaj parametar CONTENT_TYPE se u tom slucaju ne salje).
     * @param headerParamsMap                 Mapa<String, String> parametara koji se salju u header-u request-a.
     * @param byteArray                       Niz bajtova fajla
     * @param tag                             String koji svaki request obelezava razlicitim imenom. (Za slucaj ako u oviru jedne klase postoji vise postRequest metoda pa njima se moze pristupiti preko ovog indikatora).
     */
    public void createByteArrayPostRequest(String urlString, String typeOfExpectedResponse, final RequestListener postCallback,
                                           final boolean showLoadingDialog, String loadingDialogMessage, int loadingDialogStyle, int loadingDialogProgressStyle,
                                           boolean dismissLoadingDialogOnBackClick, String contentTypeString, Map<String, String> headerParamsMap, byte[] byteArray, final String tag) throws TagException, NullPointerException, IndexOutOfBoundsException {
        if (context != null) {
            if (urlString != null) {

                this.requestListenerCallback = postCallback;

                if (showLoadingDialog) {
                    LoadingDialog loadingDialog = new LoadingDialog(loadingDialogMessage, dismissLoadingDialogOnBackClick, loadingDialogStyle, loadingDialogProgressStyle, true, true);
                    if (tag != null && !tag.equals("")) {
                        for (Map.Entry<String, Object> entry : dialogsMap.entrySet()) {
                            if (tag.equalsIgnoreCase(entry.getKey())) {
                                loge("Tag already exist!");
                                throw new TagException("Tag already exist!");
                            }
                        }
                        dialogsMap.put(tag, loadingDialog);
                        showLoadingDialog(context, loadingDialog, tag);
                    }
                }

                postRequestBuilder = AndroidNetworking.post(urlString)
                        .setOkHttpClient(getOkHttpClient());

                if (contentTypeString != null && !contentTypeString.equals(""))
                    postRequestBuilder.setContentType(contentTypeString);

                if (headerParamsMap != null && headerParamsMap.size() > 0) {
                    for (Map.Entry<String, String> entry : headerParamsMap.entrySet()) {
                        postRequestBuilder.addHeaders(entry.getKey(), entry.getValue());
                    }
                }

                if (tag != null && !tag.equals(""))
                    postRequestBuilder.setTag(tag);

                if (byteArray != null) {
                    if (byteArray.length > 0)
                        postRequestBuilder.addByteBody(byteArray);
                    else
                        throw new IndexOutOfBoundsException("Byte array length is 0");
                } else {
                    throw new NullPointerException("Byte array is null");
                }

                if (typeOfExpectedResponse != null && !typeOfExpectedResponse.equals("")) {
                    switch (typeOfExpectedResponse) {

                        case "jsonObject":
                        case "jsonobject":
                        case "json":
                        case "object":

                            postRequestBuilder.build().getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }

                                    if (response != null) {
                                        logi("Successful response");
                                        postCallback.onRequestLoadSuccessful(response, tag);
                                    }

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }

                                @Override
                                public void onError(ANError anError) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }
                                    postCallback.onRequestLoadFailed(anError, tag);
                                    loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }
                            });
                            break;

                        case "jsonArray":
                        case "jsonarray":
                        case "array":

                            postRequestBuilder.build().getAsJSONArray(new JSONArrayRequestListener() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }
                                    if (response != null) {
                                        logi("Successful response");
                                        postCallback.onRequestLoadSuccessful(response, tag);
                                    }

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }

                                @Override
                                public void onError(ANError anError) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }
                                    postCallback.onRequestLoadFailed(anError, tag);
                                    loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }
                            });
                            break;

                        case "String":
                        case "string":

                            postRequestBuilder.build().getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }
                                    if (response != null) {
                                        logi("Successful response");
                                        postCallback.onRequestLoadSuccessful(response, tag);
                                    }

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }

                                @Override
                                public void onError(ANError anError) {
                                    if (showLoadingDialog) {
                                        hideLoadingDialog(tag);
                                        dialogsMap.remove(tag);
                                    }
                                    postCallback.onRequestLoadFailed(anError, tag);
                                    loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));

                                    requestListenerCallback = null;
                                    postRequestBuilder = null;
                                }
                            });
                            break;

                        default:
                            ToastMessage.toaster(context, "Invalid response type");
                            break;
                    }
                } else {
                    loge("PostRequest: Type of expected response is invalid");
                }
            } else {
                loge("PostRequest: URL is null");
            }
        } else {
            loge("PostRequest: Context is null");
        }
    }


    /**
     * Builds a get request.
     *
     * @param urlString                       Url koji gadjamo.
     * @param queryParams                     Mapa<String, Object> parametara koji se salju.
     * @param pathParams                      Mapa<String, Object> parametara koji se salju.
     * @param typeOfExpectedResponse          Naziv ocekivanog tipa response-a (Moguci su jsonObject (jsonobject, object), jsonArray (jsonarray, array), String (string)).
     * @param getCallback                     Interface za uspesno i neuspesno izvrsavanje request-a.
     * @param showLoadingDialog               boolean koji je true ako zelimo da prikazemo loading dialog.
     * @param loadingDialogMessage            Message which will be displayed in loading dialog
     * @param loadingDialogStyle              Style of loading dialog (0 is for default)
     * @param loadingDialogProgressStyle      0 for spinner, 1 for loading progress bar
     * @param dismissLoadingDialogOnBackClick true if you want to dismiss dialog with system back click
     * @param headerParamsMap                 Mapa<String, String> parametara koji se salju u header-u request-a.
     * @param tag                             String koji svaki request obelezava razlicitim imenom. (Za slucaj ako u oviru jedne klase postoji vise getRequest metoda pa njima se moze pristupiti preko ovog indikatora).
     */
    public void createGetRequest(String urlString, Map<String, Object> queryParams, Map<String, Object> pathParams, String typeOfExpectedResponse,
                                 final RequestListener getCallback, final boolean showLoadingDialog, String loadingDialogMessage,
                                 int loadingDialogStyle, int loadingDialogProgressStyle, boolean dismissLoadingDialogOnBackClick,
                                 Map<String, String> headerParamsMap, final String tag) throws TagException {
        if (urlString != null) {

            this.requestListenerCallback = getCallback;

//            if (showLoadingDialog)
//                showLoadingDialog(context);

            if (showLoadingDialog) {
                LoadingDialog loadingDialog = new LoadingDialog(loadingDialogMessage, dismissLoadingDialogOnBackClick, loadingDialogStyle, loadingDialogProgressStyle, true, true);
                if (tag != null && !tag.equals("")) {
                    for (Map.Entry<String, Object> entry : dialogsMap.entrySet()) {
                        if (tag.equalsIgnoreCase(entry.getKey())) {
                            loge("Tag already exist!");
                            throw new TagException("Tag already exist!");
                        }
                    }
                    dialogsMap.put(tag, loadingDialog);
                    showLoadingDialog(context, loadingDialog, tag);
                }
            }

            getRequestBuilder = AndroidNetworking.get(urlString)
                    .setOkHttpClient(getOkHttpClient());

            if (headerParamsMap != null && headerParamsMap.size() > 0) {
                for (Map.Entry<String, String> entry : headerParamsMap.entrySet()) {
                    getRequestBuilder.addHeaders(entry.getKey(), entry.getValue());
                }
            }

            if (tag != null && !tag.equals(""))
                getRequestBuilder.setTag(tag);

            if (queryParams != null && !queryParams.isEmpty()) {
                for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                    getRequestBuilder.addQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }

            if (pathParams != null && !pathParams.isEmpty()) {
                for (Map.Entry<String, Object> entry : pathParams.entrySet()) {
                    getRequestBuilder.addPathParameter(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }

            if (typeOfExpectedResponse != null && !typeOfExpectedResponse.equals("")) {
                switch (typeOfExpectedResponse) {

                    case "jsonObject":
                    case "jsonobject":
                    case "json":
                    case "object":

                        getRequestBuilder.build().getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (showLoadingDialog) {
                                    hideLoadingDialog(tag);
                                    dialogsMap.remove(tag);
                                }
                                if (response != null) {
                                    getCallback.onRequestLoadSuccessful(response, tag);
                                }

                                requestListenerCallback = null;
                                getRequestBuilder = null;
                            }

                            @Override
                            public void onError(ANError anError) {
                                if (showLoadingDialog) {
                                    hideLoadingDialog(tag);
                                    dialogsMap.remove(tag);
                                }
                                getCallback.onRequestLoadFailed(anError, tag);
                                loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));

                                requestListenerCallback = null;
                                getRequestBuilder = null;
                            }
                        });
                        break;

                    case "jsonArray":
                    case "jsonarray":
                    case "array":

                        getRequestBuilder.build().getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                if (showLoadingDialog) {
                                    hideLoadingDialog(tag);
                                    dialogsMap.remove(tag);
                                }
                                if (response != null) {
                                    getCallback.onRequestLoadSuccessful(response, tag);
                                }

                                requestListenerCallback = null;
                                getRequestBuilder = null;
                            }

                            @Override
                            public void onError(ANError anError) {
                                if (showLoadingDialog) {
                                    hideLoadingDialog(tag);
                                    dialogsMap.remove(tag);
                                }
                                getCallback.onRequestLoadFailed(anError, tag);
                                loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));

                                requestListenerCallback = null;
                                getRequestBuilder = null;
                            }
                        });
                        break;

                    case "String":
                    case "string":

                        getRequestBuilder.build().getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                if (showLoadingDialog) {
                                    hideLoadingDialog(tag);
                                    dialogsMap.remove(tag);
                                }
                                if (response != null) {
                                    getCallback.onRequestLoadSuccessful(response, tag);
                                }

                                requestListenerCallback = null;
                                getRequestBuilder = null;
                            }

                            @Override
                            public void onError(ANError anError) {
                                if (showLoadingDialog) {
                                    hideLoadingDialog(tag);
                                    dialogsMap.remove(tag);
                                }
                                getCallback.onRequestLoadFailed(anError, tag);
                                loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));

                                requestListenerCallback = null;
                                getRequestBuilder = null;
                            }
                        });
                        break;

                    default:
                        ToastMessage.toaster(context, "Invalid response type");
                        break;
                }
            }
        }
    }


    private void uploadFileToServer(String urlString, Map<String, Object> multipartParameters, File file, String fileName, String typeOfExpectedResponse, boolean sendAsJsonObject,
                                    final RequestUploadListener uploadCallback, final boolean showLoadingDialog, String contentTypeString, Map<String, String> headerParamsMap, final String tag) {
        if (context != null) {
            if (urlString != null) {

                this.multipartFileRequestListenerCallback = uploadCallback;

                multiPartBuilder = AndroidNetworking.upload(urlString)
                        .setOkHttpClient(getOkHttpClient());

                if (contentTypeString != null && !contentTypeString.equals(""))
                    multiPartBuilder.setContentType(contentTypeString);

                if (headerParamsMap != null && headerParamsMap.size() > 0) {
                    for (Map.Entry<String, String> entry : headerParamsMap.entrySet()) {
                        multiPartBuilder.addHeaders(entry.getKey(), entry.getValue());
                    }
                }

                if (multipartParameters != null && multipartParameters.size() > 0) {
                    for (Map.Entry<String, Object> entry : multipartParameters.entrySet()) {
                        multiPartBuilder.addMultipartParameter(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                }

                if (file != null) {
                    if (fileName != null) {
                        multiPartBuilder.addMultipartFile(fileName, file);
                    }
                }

                if (tag != null && !tag.equals("")) {
                    multiPartBuilder.setTag(tag);
                }

                if (typeOfExpectedResponse != null && !typeOfExpectedResponse.equals("")) {
                    switch (typeOfExpectedResponse) {

                        case "jsonObject":
                        case "jsonobject":
                        case "json":
                        case "object":

                            multiPartBuilder.build()
                                    .setUploadProgressListener(new UploadProgressListener() {
                                        @Override
                                        public void onProgress(long bytesUploaded, long totalBytes) {

                                        }
                                    })
                                    .getAsJSONObject(new JSONObjectRequestListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            if (response != null) {
                                                multipartFileRequestListenerCallback.onRequestUploadSuccessful(response, tag);
                                            }
                                            if (showLoadingDialog)
                                                hideLoadingDialog();

                                            multipartFileRequestListenerCallback = null;
                                            multiPartBuilder = null;
                                        }

                                        @Override
                                        public void onError(ANError anError) {
                                            multipartFileRequestListenerCallback.onRequestUploadFailed(anError, tag);
                                            loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));
                                            if (showLoadingDialog)
                                                hideLoadingDialog();

                                            multipartFileRequestListenerCallback = null;
                                            multiPartBuilder = null;
                                        }
                                    });
                            break;

                        case "jsonArray":
                        case "jsonarray":
                        case "array":

                            getRequestBuilder.build()
                                    .setUploadProgressListener(new UploadProgressListener() {
                                        @Override
                                        public void onProgress(long bytesUploaded, long totalBytes) {

                                        }
                                    })
                                    .getAsJSONArray(new JSONArrayRequestListener() {
                                        @Override
                                        public void onResponse(JSONArray response) {
                                            if (response != null) {
                                                multipartFileRequestListenerCallback.onRequestUploadSuccessful(response, tag);
                                            }
                                            if (showLoadingDialog)
                                                hideLoadingDialog();

                                            multipartFileRequestListenerCallback = null;
                                            multiPartBuilder = null;
                                        }

                                        @Override
                                        public void onError(ANError anError) {
                                            multipartFileRequestListenerCallback.onRequestUploadFailed(anError, tag);
                                            loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));
                                            if (showLoadingDialog)
                                                hideLoadingDialog();

                                            multipartFileRequestListenerCallback = null;
                                            multiPartBuilder = null;
                                        }
                                    });
                            break;

                        case "String":
                        case "string":

                            getRequestBuilder.build()
                                    .setUploadProgressListener(new UploadProgressListener() {
                                        @Override
                                        public void onProgress(long bytesUploaded, long totalBytes) {

                                        }
                                    })
                                    .getAsString(new StringRequestListener() {
                                        @Override
                                        public void onResponse(String response) {
                                            if (response != null) {
                                                multipartFileRequestListenerCallback.onRequestUploadSuccessful(response, tag);
                                            }
                                            if (showLoadingDialog)
                                                hideLoadingDialog();

                                            multipartFileRequestListenerCallback = null;
                                            multiPartBuilder = null;
                                        }

                                        @Override
                                        public void onError(ANError anError) {
                                            multipartFileRequestListenerCallback.onRequestUploadFailed(anError, tag);
                                            loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));
                                            if (showLoadingDialog)
                                                hideLoadingDialog();

                                            multipartFileRequestListenerCallback = null;
                                            multiPartBuilder = null;
                                        }
                                    });
                            break;

                        default:
                            ToastMessage.toaster(context, "Invalid response type");
                            break;
                    }
                }


            } else {
                loge("URL is null");
            }
        } else {
            loge("Context is null");
        }
    }

    public void changeDialogMessage(String newMessage) {
        loadingDialogMessage = newMessage;
    }

    /**
     * Creating default loading dialog.
     */
    private void showLoadingDialog(Context context) {
        hideLoadingDialog();
        if (loadingDialogInstance != null) {
            if (loadingDialogMessage != null) {
                loadingDialogInstance.setMessage(loadingDialogMessage);
            }
            loadingDialogInstance.show(context);
        }
    }

    private void showLoadingDialog(Context context, LoadingDialog loadingDialog, String tag) {
        hideLoadingDialog(tag);
        if (loadingDialog != null) {
            loadingDialog.show(context);
        }
    }

//    private void showLoadingDialog(Context context, String loadingDialogMessage) {
//        hideLoadingDialog();
//        if (loadingDialogInstance != null)
//            loadingDialogInstance.show(context);
//    }

//    /**
//     * Creating custom loading dialog.
//     */
//    private void showLoadingDialog(Context context, String message, int style) {
//        hideLoadingDialog();
//
//        if (loadingDialogInstance != null)
//            loadingDialogInstance.show(context);
//
////        if (message != null && !message.equals("")) {
////            loadingDialog = new LoadingDialog(message, false, style);
////            loadingDialog.show(context);
////        } else {
////            loadingDialog = new LoadingDialog("Loading, please wait...", false, style);
////            loadingDialog.show(context);
////        }
//    }

    private void hideLoadingDialog() {
        if (loadingDialogInstance != null) {
            loadingDialogInstance.hide();
        }
    }

    private void hideLoadingDialog(String tag) {
        if (dialogsMap != null && tag != null) {
            for (Map.Entry entry : dialogsMap.entrySet()) {
                if (entry.getKey().equals(tag)) {
                    LoadingDialog d = (LoadingDialog) entry.getValue();
                    d.hide();
                }
            }
        }
        if (loadingDialogInstance != null) {
            loadingDialogInstance.hide();
        }
    }

//    private void hideLoadingDialog() {
//        if (loadingDialog != null) {
//            loadingDialog.hide();
//            loadingDialog = null;
//        }
//    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
        }

        return okHttpClient;
    }

    private void loge(String message) {
        if (loggingEnabled)
            Log.e(TAG, message);
    }

    private void logi(String message) {
        if (loggingEnabled)
            Log.i(TAG, message);
    }

//    /**
//     * Builds a post request.
//     *
//     * @param urlString              Url koji gadjamo.
//     * @param bodyParams             Mapa<String, Object> parametara koji se salju.
//     * @param typeOfExpectedResponse Naziv ocekivanog tipa response-a (Moguci su jsonObject (jsonobject, object), jsonArray (jsonarray, array), String (string)).
//     * @param sendAsJSON             boolean koji je true ako podatke saljemo kao jsonObject (U ovom slucaju se bodyParams automatski konvertuju u jsonObject).
//     * @param postCallback           Interface za uspesno i neuspesno izvrsavanje request-a.
//     * @param showLoadingDialog      boolean koji je true ako zelimo da prikazemo loading dialog.
//     * @param loadingDialogMessage   poruka koja se prikazuje u dialogu
//     * @param contentTypeString      String koji predstavlja CONTENT_TYPE u request-u (Ako se ostavi prazan ovaj parametar CONTENT_TYPE se u tom slucaju ne salje).
//     * @param headerParamsMap        Mapa<String, String> parametara koji se salju u header-u request-a.
//     * @param tag                    String koji svaki request obelezava razlicitim imenom. (Za slucaj ako u oviru jedne klase postoji vise postRequest metoda pa njima se moze pristupiti preko ovog indikatora).
//     */
//    public void createPostRequest(String urlString, Map<String, Object> bodyParams, String typeOfExpectedResponse, boolean sendAsJSON,
//                                  final RequestListener postCallback, final boolean showLoadingDialog, String loadingDialogMessage, int loadingDialogStyle, String contentTypeString, Map<String, String> headerParamsMap, final String tag) {
//        if (context != null) {
//            if (urlString != null) {
//
//                this.requestListenerCallback = postCallback;
//
//                if (showLoadingDialog)
//                    showLoadingDialog(context, loadingDialogMessage, loadingDialogStyle);
//
//                postRequestBuilder = AndroidNetworking.post(urlString)
//                        .setOkHttpClient(getOkHttpClient());
//
//                if (contentTypeString != null && !contentTypeString.equals(""))
//                    postRequestBuilder.setContentType(contentTypeString);
//
//                if (headerParamsMap != null && headerParamsMap.size() > 0) {
//                    for (Map.Entry<String, String> entry : headerParamsMap.entrySet()) {
//                        postRequestBuilder.addHeaders(entry.getKey(), entry.getValue());
//                    }
//                }
//
//                if (tag != null && !tag.equals(""))
//                    postRequestBuilder.setTag(tag);
//
//                Map<String, Object> mainMap = new HashMap<>();
//
//                if (bodyParams != null)
//                    mainMap.putAll(bodyParams);
//
//                if (sendAsJSON) {
//                    JSONObject jsonObject = new JSONObject(mainMap);
//
//                    try {
//                        for (Map.Entry<String, Object> entry : mainMap.entrySet()) {
//                            jsonObject.put(entry.getKey(), entry.getValue());
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    postRequestBuilder.addStringBody(String.valueOf(jsonObject));
//                    logi("String body added");
//                } else if (!mainMap.isEmpty()) {
//                    for (Map.Entry<String, Object> entry : mainMap.entrySet()) {
//                        postRequestBuilder.addBodyParameter(entry.getKey(), String.valueOf(entry.getValue()));
//                    }
//                    logi("Parameters added");
//                }
//
//                if (typeOfExpectedResponse != null && !typeOfExpectedResponse.equals("")) {
//                    switch (typeOfExpectedResponse) {
//
//                        case "jsonObject":
//                        case "jsonobject":
//                        case "json":
//                        case "object":
//
//                            postRequestBuilder.build().getAsJSONObject(new JSONObjectRequestListener() {
//                                @Override
//                                public void onResponse(JSONObject response) {
//                                    if (response != null) {
//                                        logi("Successful response");
//                                        postCallback.onRequestLoadSuccessful(response, tag);
//                                    }
//                                    if (showLoadingDialog)
//                                        hideLoadingDialog();
//
//                                    requestListenerCallback = null;
//                                    postRequestBuilder = null;
//                                }
//
//                                @Override
//                                public void onError(ANError anError) {
//                                    postCallback.onRequestLoadFailed(anError, tag);
//                                    loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));
//                                    if (showLoadingDialog)
//                                        hideLoadingDialog();
//
//                                    requestListenerCallback = null;
//                                    postRequestBuilder = null;
//                                }
//                            });
//                            break;
//
//                        case "jsonArray":
//                        case "jsonarray":
//                        case "array":
//
//                            postRequestBuilder.build().getAsJSONArray(new JSONArrayRequestListener() {
//                                @Override
//                                public void onResponse(JSONArray response) {
//                                    if (response != null) {
//                                        logi("Successful response");
//                                        postCallback.onRequestLoadSuccessful(response, tag);
//                                    }
//                                    if (showLoadingDialog)
//                                        hideLoadingDialog();
//
//                                    requestListenerCallback = null;
//                                    postRequestBuilder = null;
//                                }
//
//                                @Override
//                                public void onError(ANError anError) {
//                                    postCallback.onRequestLoadFailed(anError, tag);
//                                    loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));
//                                    if (showLoadingDialog)
//                                        hideLoadingDialog();
//
//                                    requestListenerCallback = null;
//                                    postRequestBuilder = null;
//                                }
//                            });
//                            break;
//
//                        case "String":
//                        case "string":
//
//                            postRequestBuilder.build().getAsString(new StringRequestListener() {
//                                @Override
//                                public void onResponse(String response) {
//                                    if (response != null) {
//                                        logi("Successful response");
//                                        postCallback.onRequestLoadSuccessful(response, tag);
//                                    }
//                                    if (showLoadingDialog)
//                                        hideLoadingDialog();
//
//                                    requestListenerCallback = null;
//                                    postRequestBuilder = null;
//                                }
//
//                                @Override
//                                public void onError(ANError anError) {
//                                    postCallback.onRequestLoadFailed(anError, tag);
//                                    loge("onError " + "-> errorCode:" + String.valueOf(anError.getErrorCode()) + ", error:" + String.valueOf(anError.getErrorDetail()));
//                                    if (showLoadingDialog)
//                                        hideLoadingDialog();
//
//                                    requestListenerCallback = null;
//                                    postRequestBuilder = null;
//                                }
//                            });
//                            break;
//
//                        default:
//                            ToastMessage.toaster(context, "Invalid response type");
//                            break;
//                    }
//                } else {
//                    loge("PostRequest: Type of expected response is invalid");
//                }
//            } else {
//                loge("PostRequest: URL is null");
//            }
//        } else {
//            loge("PostRequest: Context is null");
//        }
//    }

    public void recreateLoadingDialog(String loadingDialogTag) {
        if (loadingDialogInstance != null) {
            loadingDialogInstance.setMessage("test");
        }
    }

}
