package com.example.zjj20181218.icustody.util;


import android.app.Activity;
import android.util.Log;

import com.example.zjj20181218.icustody.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Zjj on 2018/12/18.
 */


public class HttpUtil {

    /**
     * 向服务器发送post请求，包含一些Map参数
     *
     * @param address
     * @param callback
     * @param map
     */
    public static void post(String address, okhttp3.Callback callback, Map<String, String> map) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        if (map != null) {
            //添加POST中传送过去的一些键值对信息
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static Response post(String address, String filed, String data) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add(filed, data)
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();

        return client.newCall(request).execute();
    }

    public static Call post(String address, String[] filed, String[] data) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add(filed[0], data[0])
                .add(filed[1], data[1])
                .add(filed[2], data[2])
                .add(filed[3], data[3])
                .add(filed[4], data[4])
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        return client.newCall(request);
    }

    /**
     * 向服务器发送json数据
     *
     * @param address  地址
     * @param callback 回调
     * @param jsonStr  json数据
     */
    public static void postJson(String address, okhttp3.Callback callback, String jsonStr) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //OKHttp同步网络请求，post方式
    public static Response synPostJson(String address, String jsonStr) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        /*try {
            Response response = client.newCall(request).execute();
            str = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return client.newCall(request).execute();
    }


    public static Call uploadImage(String url, List<String> imagePath, Activity activity) throws IOException, JSONException {
        File sdcache = activity.getExternalCacheDir();
        int cacheSize = 10 * 1024 * 1024;
        //设置超时时间及缓存
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .cache(new Cache(sdcache.getAbsoluteFile(), cacheSize));

        OkHttpClient mOkHttpClient = builder.build();

        MultipartBody.Builder mbody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("userid", MyApplication.user.getId())
                .addFormDataPart("count", String.valueOf(imagePath.size()));

        List<File> fileList = new ArrayList<>();
        for (int i = 0; i < imagePath.size(); i++) {
            fileList.add(new File(imagePath.get(i)));
        }
        int i = 0;
        for (File file : fileList) {
            if (file.exists()) {
                Log.i("imageName:", file.getName());//经过测试，此处的名称不能相同，如果相同，只能保存最后一个图片，不知道那些同名的大神是怎么成功保存图片的。
                mbody.addFormDataPart("image" + i, file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file));
                i++;
            }
        }
        RequestBody requestBody = mbody.build();
        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + "...")
                .url(url)
                .post(requestBody)
                .build();
        return mOkHttpClient.newCall(request);
    }
}