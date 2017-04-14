package com.team3.fastcampus.record.Util;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
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
    private int method = GET; // Default is GET
    private Map<String, Object> params;
    private Headers.Builder headersBuilder;
    private List<StatusCallback> statusCallbacks;

    private Disposable disposable;

    private NetworkController(String url) {
        init(url);
    }

    public static NetworkController newInstance(String url) {
        return new NetworkController(url);
    }

    /**
     * NetworkController 초기화
     *
     * @return
     */
    public NetworkController init() {
        return init(url);
    }

    /**
     * NetworkController 초기화
     *
     * @param url
     * @return
     */
    public NetworkController init(String url) {
        if (!isDisposed()) {
            throw new RuntimeException("this NetworkController is using");
        }
        this.url = urlSetting(url);
        params = new HashMap<>(); // params 초기화
        headersBuilder = new Headers.Builder();
        statusCallbacks = new ArrayList<>();

        return this;
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
     * Header 를 완전 초기화
     *
     * @return
     */
    public NetworkController headerInit() {
        headersBuilder = new Headers.Builder();

        return this;
    }

    /**
     * Header 를 해당 내용으로 초기화
     *
     * @param header
     * @return
     */
    public NetworkController headerSet(Map<String, String> header) {
        headerInit();

        Set<String> keys = header.keySet();
        for (String key : keys) {
            headerAdd(key, header.get(key));
        }

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
     * Header 를 여러개를 한번에 추가
     *
     * @param header
     * @return
     */
    public NetworkController headerAdd(Map<String, String> header) {
        Set<String> keys = header.keySet();
        for (String key : keys) {
            headerAdd(key, header.get(key));
        }

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
     *
     * @param key
     * @param value
     * @return
     */
    public NetworkController paramsAdd(String key, Object value) {
        params.put(key, value);

        return this;
    }

    /**
     * Body Params 여러개 추가하기
     *
     * @param params
     * @return
     */
    public NetworkController paramsAdd(Map<String, Object> params) {
        Set<String> keys = params.keySet();
        for (String key : keys) {
            paramsAdd(key, params.get(key));
        }

        return this;
    }

    /**
     * StatusCallback을 초기화
     *
     * @return
     */
    public NetworkController initCallback() {
        statusCallbacks.clear();

        return this;
    }

    /**
     * StatusCallback을 추가 한다.
     *
     * @param statusCallback
     * @return
     */
    public NetworkController addCallback(StatusCallback statusCallback) {
        statusCallbacks.add(statusCallback);

        return this;
    }

    /**
     * callback의 error를 실행 (Used Observer Pattern)
     *
     * @param error
     */
    private void callbackError(Throwable error) {
        for (StatusCallback statusCallback : statusCallbacks) {
            if (statusCallback != null) {
                statusCallback.onError(error);
            }
        }
    }

    /**
     * callback의 success를 실행 (Used Observer Pattern)
     *
     * @param responseData
     */
    private void callbackSuccess(ResponseData responseData) {
        for (StatusCallback statusCallback : statusCallbacks) {
            if (statusCallback != null) {
                statusCallback.onSuccess(responseData);
            }
        }
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
     * Http통신을 위한 url 설정
     * @param url
     * @return
     */
    public String urlSetting(String url) {
        if(!url.startsWith("http")){
            url = "http://" + url;
        }
        return url;
    }

    /**
     * Header 를 Build 한다.
     *
     * @param requestBuilder
     * @return
     */
    private Request.Builder buildHeader(Request.Builder requestBuilder) {
        return requestBuilder.headers(headersBuilder.build());
    }

    /**
     * GET Type에 맞도록 Build한다.
     *
     * @param requestBuilder
     * @return
     */
    private Request.Builder buildGET(Request.Builder requestBuilder) {
        if (params != null) {
            String urlParams = "?";
            for (String key : params.keySet()) {
                urlParams += key + "=" + params.get(key) + '&';
            }
            urlParams = urlParams.substring(0, urlParams.length() - 1);
            url = url + urlParams;
            requestBuilder.url(url);
        }

        return requestBuilder.get();
    }

    /**
     * POST Type에 맞도록 Build한다.
     *
     * @param requestBuilder
     * @return
     */
    private Request.Builder buildPOST(Request.Builder requestBuilder) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (params != null) {
            for (String key : params.keySet()) {
                formBuilder.addEncoded(key, params.get(key).toString());
            }
        }
        return requestBuilder.post(formBuilder.build());
    }

    /**
     * Request를 만들어 낸다.
     *
     * @return
     */
    private Request buildRequest() {
        Request.Builder requestBuilder = new Request.Builder();

        requestBuilder.url(url);

        buildHeader(requestBuilder);

        switch (method) {
            case GET:
                buildGET(requestBuilder);
                break;
            case POST:
                buildPOST(requestBuilder);
                break;
            default:
                throw new RuntimeException("NetworkController is support GET or POST only");
        }

        return requestBuilder.build();
    }

    /**
     * Http 통신 시작
     * callback은 mainThread로 지정한다.
     */
    public void execute() {
        execute(AndroidSchedulers.mainThread());
    }

    /**
     * Http 통신 시작
     * Scheduler로 callback의 Thread를 지정 해준다.
     *
     * @param scheduler
     */
    public void execute(Scheduler scheduler) {
        if (disposable != null && !disposable.isDisposed()) {
            callbackError(new Throwable("disposable is using"));
            return;
        }

        disposable = Observable.fromCallable(() -> {
            OkHttpClient client = new OkHttpClient();
            return new ResponseData(client.newCall(buildRequest()).execute());
        }).subscribeOn(Schedulers.io())
                .cast(ResponseData.class)
                .doOnNext(response -> response.body = response.response.body().bytes())
                .observeOn(scheduler)
                .subscribe(responseData -> {
                    callbackSuccess(responseData);
                }, error -> {
                    callbackError(error);
                    callbackError(error);
                });
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
     * Decoding by Gson
     *
     * @param type
     * @param json
     * @param <T>
     * @return ClassData
     * @throws JsonSyntaxException
     */
    public static <T> T decode(Type type, String json) throws JsonSyntaxException {
        return new Gson().fromJson(json, type);
    }

    /**
     * Decoding by JSONObject
     *
     * @param json
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject decodeObject(String json) throws JSONException {
        return new JSONObject(json);
    }

    /**
     * Decoding by JSONArray
     *
     * @param json
     * @return JSONArray
     * @throws JSONException
     */
    public static JSONArray decodeArray(String json) throws JSONException {
        return new JSONArray(json);
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
     * Encoding by JSONArray
     *
     * @param jsonArray
     * @return
     * @throws JSONException
     */
    public static List<Map<String, Object>> encode(JSONArray jsonArray) throws JSONException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(encode(jsonArray.getJSONObject(i)));
        }
        return list;
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
     * 통신 상태 결과 Callback
     */
    public interface StatusCallback {
        void onError(Throwable error);

        void onSuccess(ResponseData responseData);
    }

    /**
     * Callback해줄 응답 데이터
     */
    public class ResponseData {

        /**
         * excute되어 반환되는 body
         */
        public byte[] body;

        /**
         * close된 response
         */
        public Response response;

        public ResponseData(Response response) {
            this.response = response;
        }
    }
}
