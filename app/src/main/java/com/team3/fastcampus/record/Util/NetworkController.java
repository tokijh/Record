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
import okhttp3.Headers;
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
    private int method = GET; // Defaul is GET
    private Map<String, Object> params;
    private Headers.Builder headersBuilder;

    private Disposable disposable;

    private NetworkController(String url) {
        if(!url.startsWith("http")){
            url = "http://" + url;
        }
        this.url = url;

        params = new HashMap<>(); // params 초기화
        headersBuilder = new Headers.Builder();
    }

    public static NetworkController newInstance(String url) {
        return new NetworkController(url);
    }

    private void destroy() {
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
    }

    /**
     * 인터넷 연결 상태를 가져온다.
     *
     * ENABLE 연결인 경우에는 (networkStatus & NETWORK_ENABLE > 0) 가 true인 경우
     *
     * WIFI 연결인 경우에는 (networkStatus & NETWORK_WIFI > 0) 가 true인 경우
     * MOBILE 연결인 경우에는 (networkStatus & NETWORK_MOBILE > 0) 가 true인 경우
     *
     * @param context
     * @return
     */
    public static int checkNetworkStatus(Context context) {
        int networkStatus = NETWORK_DISABLE;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
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
        }

        return networkStatus;
    }

    /**
     * ENABLE인지 아닌지 판단
     *
     * @param networkStatus
     * @return
     */
    public static boolean isNetworkStatusENABLE(int networkStatus) {
        if ((networkStatus & NETWORK_ENABLE) > 0) {
            return true;
        }
        return false;
    }

    /**
     * disposable이 Disposed인지 확인
     *
     * @return
     */
    public boolean isDisposed() {
        return disposable == null || disposable.isDisposed();
    }

    /**
     * Method 설정
     * @param method
     * @return
     */
    public NetworkController setMethod(int method) {
        switch (method) {
            case GET:
                break;
            case POST:
                break;
            default:
                throw new RuntimeException("NetworkController is support GET or POST only");
        }
        this.method = method;

        return this;
    }

    /**
     * Body Param 를 완전 초기화
     *
     * @return
     */
    public NetworkController paramsInit() {
        params = new HashMap<>();

        return this;
    }

    /**
     * Body Params 설정
     *
     * @param params
     * @return
     */
    public NetworkController paramsSet(Map<String, Object> params) {
        this.params = params;

        return this;
    }

    /**
     * Body Param 추가
     * @param key
     * @param value
     * @return
     */
    public NetworkController paramsAdd(String key, Object value) {
        params.put(key, value);

        return this;
    }

    /**
     * Header 를 완전 초기화
     *
     * @return
     */
    public NetworkController headerInit() {
        headersBuilder = new Headers.Builder();

        return this;
    }

    /**
     * Header 를 line단위로 추가
     *
     * @param line
     * @return
     */
    public NetworkController headerAdd(String line) {
        headersBuilder.add(line);

        return this;
    }

    /**
     * Header 를 추가
     *
     * @param name
     * @param value
     * @return
     */
    public NetworkController headerAdd(String name, String value) {
        headersBuilder.add(name, value);

        return this;
    }

    /**
     * Header 의 해당 name을 가진 내용 모두 삭제
     *
     * @param name
     * @return
     */
    public NetworkController headerRemoveAll(String name) {
        headersBuilder.removeAll(name);

        return this;
    }

    /**
     * Http통신 시작
     *
     * @param method
     * @param datas null시 전송 파라미터 없음.
     * @param statusCallback null시 callback없음.
     */
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
                subscriber.onError(e);
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
