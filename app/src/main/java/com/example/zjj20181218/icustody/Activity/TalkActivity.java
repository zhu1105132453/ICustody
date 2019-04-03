package com.example.zjj20181218.icustody.Activity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.example.zjj20181218.icustody.MyApplication;
import com.example.zjj20181218.icustody.R;
import com.example.zjj20181218.icustody.adapter.TalkAdapter;
import com.example.zjj20181218.icustody.javaBean.Talk;
import com.example.zjj20181218.icustody.javaBean.User;
import com.example.zjj20181218.icustody.util.HttpUtil;
import com.example.zjj20181218.icustody.util.db.TalkDao;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TalkActivity extends BaseActivity {

    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;

    private List<String> mImages;
    private GridLayoutManager manager;
    private TalkAdapter adapter;
    private RecyclerView recyclerView;
    private EditText text;
    private TextView choose;
    private TextView cancel;
    private TextView title;
    private TextView push;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);

        initView();
    }

    private void initView() {
        mImages = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.images);
        text = (EditText) findViewById(R.id.text);
        choose = (TextView) findViewById(R.id.add_image);
        cancel = (TextView) findViewById(R.id.cancel);
        title = (TextView) findViewById(R.id.title);
        push = (TextView) findViewById(R.id.push);
        manager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(manager);
        adapter = new TalkAdapter(mImages);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        String titleText = intent.getStringExtra("title");
        title.setText(titleText);

        choose.setClickable(true);
        cancel.setClickable(true);
        push.setClickable(true);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertView("上传头像", null, "取消", null,
                        new String[]{"拍照", "从相册中选择"}, TalkActivity.this, AlertView.Style.ActionSheet, new OnItemClickListener() {
                    public void onItemClick(Object o, int position) {
                        switch (position) {
                            case 0:
                                //拍照获取图片
                                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                                try {
                                    if (outputImage.exists()) {
                                        outputImage.delete();
                                    }
                                    outputImage.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (Build.VERSION.SDK_INT >= 24) {
                                    imageUri = FileProvider.getUriForFile(TalkActivity.this, "com.example.zjj20181218.icustody.fileprovider", outputImage);
                                } else {
                                    imageUri = Uri.fromFile(outputImage);
                                }
                                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                intent.putExtra("imageUri", imageUri.toString());
                                Log.i("a", imageUri.toString() + "aa");
                                startActivityForResult(intent, TAKE_PHOTO);
                                break;
                            case 1:
                                //从本地获取图片
                                openAlbum();
                                break;
                            default:
                                break;
                        }
                    }
                }).setCancelable(true).show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertView("", "确定放弃编辑？", null, new String[]{"确定", "取消"}, null, TalkActivity.this,
                        AlertView.Style.Alert, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        if (position == 0) {
                            ActivityCollector.removeActivity(TalkActivity.this);
                        }
                    }
                }).setCancelable(true).show();
            }
        });

        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String content = text.getText().toString();
                try {
                    HttpUtil.uploadImages("http://120.79.229.78/zjj/android/database/imagesurl.php", mImages, content, TalkActivity.this).enqueue(new Callback() {

                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.i("hh", "请求上传说说失败");
                            ActivityCollector.removeActivity(TalkActivity.this);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            Log.i("hh", "请求上传说说成功" + result);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                            // 获取当前时间
                            Date date = new Date(System.currentTimeMillis());

                            StringBuffer imagePath = new StringBuffer();
                            for (String imgPath : mImages) {
                                imagePath.append(imgPath);
                            }
                            //说说保存至本地数据库
                            Talk talk = new Talk(Integer.parseInt(MyApplication.user.getId()), content, String.valueOf(imagePath), simpleDateFormat.format(date));
                            TalkDao talkDao = new TalkDao(TalkActivity.this);
                            talkDao.insert(talk);
                            ActivityCollector.removeActivity(TalkActivity.this);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        if (imageUri != null) {
                            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                            //displayImage(getImagePath(imageUri, new String[]{MediaStore.Images.ImageColumns.DATA}, null));
                            displayImage(getFPUriToPath(this, imageUri));
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (data != null) {
                    Uri uri = data.getData();
                    Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
                    displayImage(getPathByUri(uri)); // 根据图片路径显示图片
                }

        }
    }

    /**
     * 获取FileProvider path
     * author zx
     * version 1.0
     * since 2018/5/4  .
     */
    private static String getFPUriToPath(Context context, Uri uri) {
        try {
            List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
            if (packs != null) {
                String fileProviderClassName = FileProvider.class.getName();
                for (PackageInfo pack : packs) {
                    ProviderInfo[] providers = pack.providers;
                    if (providers != null) {
                        for (ProviderInfo provider : providers) {
                            if (uri.getAuthority().equals(provider.authority)) {
                                if (provider.name.equalsIgnoreCase(fileProviderClassName)) {
                                    Class<FileProvider> fileProviderClass = FileProvider.class;
                                    try {
                                        Method getPathStrategy = fileProviderClass.getDeclaredMethod("getPathStrategy", Context.class, String.class);
                                        getPathStrategy.setAccessible(true);
                                        Object invoke = getPathStrategy.invoke(null, context, uri.getAuthority());
                                        if (invoke != null) {
                                            String PathStrategyStringClass = FileProvider.class.getName() + "$PathStrategy";
                                            Class<?> PathStrategy = Class.forName(PathStrategyStringClass);
                                            Method getFileForUri = PathStrategy.getDeclaredMethod("getFileForUri", Uri.class);
                                            getFileForUri.setAccessible(true);
                                            Object invoke1 = getFileForUri.invoke(invoke, uri);
                                            if (invoke1 instanceof File) {
                                                String filePath = ((File) invoke1).getAbsolutePath();
                                                return filePath;
                                            }
                                        }
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getImagePath(Uri uri, String[] projection, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = this.getContentResolver().query(uri, projection, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Log.d("aaasdasdasdas", imagePath);
            if (mImages.size() <= 9) {
                mImages.add(imagePath);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "只能输入至多9张图片", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
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

}
