package com.dongah.fastcharger.websocket.socket;

import androidx.annotation.NonNull;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClientHelper {
    public static final Logger logger = LoggerFactory.getLogger(HttpClientHelper.class);

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();

    private boolean reTry = false;

    public interface HttpCallback {
        void onSuccess(int statusCode, String response);
        void onFailure(IOException e);
    }


    // 기본 POST 메서드 (재시도 없음)
    public void post(String url, String jsonBody, HttpCallback callback) {
        postWithRetry(url, jsonBody,  callback);  // 재시도 없음
    }


    public void postWithRetry(String url, String jsonBody, HttpCallback callback) {
        RequestBody requestBody = RequestBody.create(JSON, jsonBody);
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (!reTry) {
                    postWithRetry(url, jsonBody , callback);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    if (callback != null) callback.onFailure(e);
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                int statusCode = response.code();
                assert response.body() != null;
                String responseBody = response.body().string();

                if (statusCode != 200 && !reTry) {
                    postWithRetry(url, jsonBody,  callback);

                } else {
                    if (callback != null) {
                        callback.onSuccess(statusCode, responseBody);
                        reTry = true;
                    }
                }
            }
        });

    }

    public String onJsonMake(String key, String value) {
        String result = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(key, value);

            result = jsonObject.toString();

        } catch (Exception e) {
            logger.error(" onJsonMake error : {}", e.getMessage());
        }
        return result;
    }
}
