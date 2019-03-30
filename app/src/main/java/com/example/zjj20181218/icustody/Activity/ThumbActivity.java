package com.example.zjj20181218.icustody.Activity;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.zjj20181218.icustody.MyApplication;
import com.example.zjj20181218.icustody.R;
import com.example.zjj20181218.icustody.javaBean.User;
import com.example.zjj20181218.icustody.util.HttpUtil;
import com.example.zjj20181218.icustody.util.view.ClipViewLayout;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ThumbActivity extends BaseActivity implements View.OnClickListener {

    private ClipViewLayout clipViewLayout1;
    private ClipViewLayout clipViewLayout2;
    private TextView btnCancel;
    private TextView btnOk;
    //类别 1: qq, 2: weixin
    private int type = 2;
    private String imagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumb);

        Intent intent = getIntent();
        imagePath = intent.getStringExtra("imagePath");

        initView();
    }

    /**
     * 初始化组件
     */
    public void initView() {
        clipViewLayout1 = (ClipViewLayout) findViewById(R.id.clipViewLayout1);
        clipViewLayout2 = (ClipViewLayout) findViewById(R.id.clipViewLayout2);
        btnCancel = (TextView) findViewById(R.id.btn_cancel);
        btnOk = (TextView) findViewById(R.id.bt_ok);
        //设置点击事件监听器
        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        clipViewLayout2.setVisibility(View.VISIBLE);
        clipViewLayout1.setVisibility(View.GONE);
        //设置图片资源
        clipViewLayout2.setImageSrc(imagePath);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.bt_ok:
                generateUriAndReturn();
                break;
        }
    }


    /**
     * 生成Uri并且通过setResult返回给打开的activity
     */
    private void generateUriAndReturn() {
        //调用返回剪切图
        Bitmap zoomedCropBitmap;
        if (type == 1) {
            zoomedCropBitmap = clipViewLayout1.clip();
        } else {
            zoomedCropBitmap = clipViewLayout2.clip();
        }
        if (zoomedCropBitmap == null) {
            Log.e("android", "zoomedCropBitmap == null");
            return;
        }
        Uri mSaveUri = Uri.fromFile(new File(getCacheDir(), "cropped_" + System.currentTimeMillis() + ".jpg"));
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(mSaveUri);
                if (outputStream != null) {
                    zoomedCropBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                }
            } catch (IOException ex) {
                Log.e("android", "Cannot open file: " + mSaveUri, ex);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            //上传头像
            List<String> list = new ArrayList<>();
            Log.i("?", String.valueOf(mSaveUri));
            list.add(getPathByUri(mSaveUri));
            try {
                HttpUtil.uploadImage("http://120.79.229.78/zjj/android/database/images.php", list, this).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i("hh", "请求上传头像失败");
                        finish();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Log.i("hh", "请求上传头像成功" + result);
                        Response responseUser = null;
                        try {
                            responseUser = HttpUtil.post(getString(R.string.userAddress), "username", MyApplication.user.getEmail());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        if (responseUser != null) {
                            String receive = null;
                            try {
                                receive = responseUser.body().string();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            //json数据解析
                            Log.i("aaa", receive);
                            Gson gson1 = new Gson();
                            User user = gson1.fromJson(receive, User.class);
                            if (!user.getThumb().equals(MyApplication.user.getThumb())){
                                MyApplication.user = user;
                                finish();
                            }
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPathByUri(Uri uri) {
        String imagePath = null;
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    private String getImagePath(Uri uri, String[] projection, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, projection, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

}
