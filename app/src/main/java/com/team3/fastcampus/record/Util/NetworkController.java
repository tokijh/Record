package com.team3.fastcampus.record.Util;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

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

    private String url;

    private Disposable disposable;

    private NetworkController(String url) {
        this.url = url;
    }

    public static NetworkController newInstance(String url) {
        return new NetworkController(url);
    }

    private void destroy() {
        if (disposable != null && !disposable.isDisposed())
            disposable.dispose();
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
            OkHttpClient client = new OkHttpClient();

            Response response = client.newCall(buildRequest(method, datas)).execute();

            String result = response.body().string();

            response.close();

            subscriber.onNext(result);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (statusCallback != null) {
                        statusCallback.onSuccess(response.toString());
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
    public <T> T decode(Class<T> clazz, String json) throws JsonSyntaxException {
        return new Gson().fromJson(json, clazz);
    }

    /**
     * Decoding by JSONObject
     *
     * @param json
     * @return JSONObject
     * @throws JSONException
     */
    public JSONObject decode(String json) throws JSONException {
        return new JSONObject(json);
    }

    /**
     * Encoding by JSONObject
     *
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public Map<String, Object> encode(JSONObject jsonObject) throws JSONException {
        Map<String, Object> data = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            data.put(key, jsonObject.get(key));
        }
        return data;
    }

    /**
     * Encoding from object class by Gson
     * @param object
     * @param <T>
     * @return
     * @throws JSONException
     */
    public <T> Map<String, Object> encode(T object) throws JSONException {
        return encode(new Gson().toJson(object));
    }

    /**
     * Encoding from String
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public Map<String, Object> encode(String json) throws JSONException {
        return encode(new JSONObject(json));
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

        void onSuccess(String response);
    }
}
