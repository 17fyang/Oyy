package com.stu.oyy.util;

import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.stu.oyy.web.IRestResponse;
import com.stu.oyy.web.Rest;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author: 乌鸦坐飞机亠
 * @date: 2020/12/7 19:05
 * @Description:
 */
@SuppressWarnings("all")
public class HttpUtil {
    public static final int CONNECT_TIMEOUT = 5;

    /**
     * 异步发送post请求,返回rest格式数据
     *
     * @param url
     * @param body
     * @param restResponse
     */
    public static void asyncPostRest(String url, RequestBody body, IRestResponse restResponse) {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("net", "fail to connect to server " + url);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (restResponse == null) return;
                try {
                    Rest rest = JSON.parseObject(response.body().string(), Rest.class);
                    restResponse.onRestResponse(rest);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("net", "there is a exception on running callback " + url);
                }
            }
        });
    }

    /**
     * 同步发送post请求
     *
     * @param url
     * @param body
     * @return
     */
    public static Rest syncPost(String url, RequestBody body) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return syncPost(request);
    }

    /**
     * 同步发送post请求
     *
     * @param url
     * @param body
     * @param request
     * @return
     */
    public static Rest syncPost(Request request) {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS).build();
        Call call = client.newCall(request);
        try {
            String text = call.execute().body().string();

            return JSON.parseObject(text, Rest.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}


