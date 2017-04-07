package com.team3.fastcampus.record.Util;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * HTTP 통신을 위한 컨트롤러
 */
public class NetworkController {

    public static final int GET = 0;
    public static final int POST = 1;

    public static final int NETWORK_DISABLE = 0x00;
    public static final int NETWORK_ENABLE = 0x08;
    public static final int NETWORK_WIFI = 0x01;
    public static final int NETWORK_MOBILE = 0x02;

    private String url;

    private Disposable disposable;

    private NetworkController(String url) {
        if(!url.startsWith("http")){
            url = "http://" + url;
        }
        this.url = url;
    }

    public static NetworkController newInstance(String url) {
        return new NetworkController(url);
    }

    private void destroy() {
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
    }

    public static int checkNetworkStatus(Context context) {
        int networkStatus = NETWORK_DISABLE;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        switch (networkInfo.getType()) {
            case ConnectivityManager.TYPE_WIFI:
                networkStatus |= NETWORK_WIFI;
                break;
            case ConnectivityManager.TYPE_MOBILE:
                networkStatus |= NETWORK_MOBILE;
        }
        if (networkInfo.getState() == NetworkInfo.State.CONNECTED || networkInfo.getState() == NetworkInfo.State.CONNECTING) {
            networkStatus |= NETWORK_ENABLE;
        }

        return networkStatus;
    }

    public boolean isDisposed() {
        return disposable == null || disposable.isDisposed();
    }

    public void excute(int method, @Nullable Map<String, Object> datas, @Nullable StatusCallback statusCallback) {
        if (disposable != null && !disposable.isDisposed()) {
            if (statusCallback != null) {
                statusCallback.onError(new Throwable("disposable is using"));
            }
            return;
        }

        disposable = Observable.create(subscriber -> {
            try {
                OkHttpClient client = new OkHttpClient();

                Response response = client.newCall(buildRequest(method, datas)).execute();

                subscriber.onNext(response);
            } catch (IOException e) {
                if (statusCallback != null) {
                    statusCallback.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (statusCallback != null) {
                        statusCallback.onSuccess((Response) response);
                    }
                    destroy();
                }, error -> {
                    if (statusCallback != null) {
                        statusCallback.onError(error);
                    }
                    destroy();
                });
    }

    /**
     * Decoding by Gson
     *
     * @param clazz
     * @param json
     * @param <T>
     * @return ClassData
     * @throws JsonSyntaxException
     */
    public static <T> T decode(Class<T> clazz, String json) throws JsonSyntaxException {
        return new Gson().fromJson(json, clazz);
    }

    /**
     * Decoding by JSONObject
     *
     * @param json
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject decode(String json) throws JSONException {
        return new JSONObject(json);
    }

    /**
     * Encoding by JSONObject
     *
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static Map<String, Object> encode(JSONObject jsonObject) throws JSONException {
        Map<String, Object> data = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            data.put(key, jsonObject.get(key));
        }
        return data;
    }

    /**
     * Encoding from String
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public static Map<String, Object> encode(String json) throws JSONException {
        return encode(new JSONObject(json));
    }

    /**
     * Encoding from object class by Gson
     * @param object
     * @param <T>
     * @return
     * @throws JSONException
     */
    public static <T> Map<String, Object> encode(T object) throws JSONException {
        return encode(new Gson().toJson(object));
    }

    /**
     * 서버에 보낼 Request만들기
     *
     * @param method 전송 방식 GET OR POST
     * @param datas 함께 전송할 params
     * @return OKHttp3의 Request
     */
    private Request buildRequest(int method, Map<String, Object> datas) {
        Request.Builder requestBuilder = new Request.Builder();

        if (method == GET) {
            if (datas != null) {
                String params = "?";
                for (String key : datas.keySet()) {
                    params += key + "=" + datas.get(key) + '&';
                }
                params = params.substring(0, params.length() - 1);
                url = url + params;
            }
            requestBuilder.get();
        } else if (method == POST) {
            FormBody.Builder formBuilder = new FormBody.Builder();
            if (datas != null) {
                for (String key : datas.keySet()) {
                    formBuilder.addEncoded(key, datas.get(key).toString());
                }
            }
            requestBuilder.post(formBuilder.build());
        } else {
            throw new RuntimeException("NetworkController is support GET or POST only");
        }

        requestBuilder.url(url);

        return requestBuilder.build();
    }

    /**
     * 통신 상태 결과 Callback
     */
    public interface StatusCallback {
        void onError(Throwable error);

        void onSuccess(Response response);
    }
}
