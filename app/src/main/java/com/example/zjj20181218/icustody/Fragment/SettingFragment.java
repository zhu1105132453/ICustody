package com.example.zjj20181218.icustody.Fragment;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.zjj20181218.icustody.Activity.ActivityCollector;
import com.example.zjj20181218.icustody.Activity.LoginActivity;
import com.example.zjj20181218.icustody.Activity.ThumbActivity;
import com.example.zjj20181218.icustody.MyApplication;
import com.example.zjj20181218.icustody.R;
import com.example.zjj20181218.icustody.adapter.SettingAdapter;
import com.example.zjj20181218.icustody.javaBean.Content;
import com.example.zjj20181218.icustody.javaBean.User;
import com.example.zjj20181218.icustody.util.GlideCircleTransform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by maxliaops on 17-1-6.
 */

public class SettingFragment extends Fragment {

    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private static final int THUMB = 3;

    private List<Content> contentList = new ArrayList<>();
    private View view;
    private ImageView thumb;
    private LinearLayout image;
    private LinearLayout login_out;

    //用户属性
    private User user = MyApplication.user;

    private Uri imageUri;
    private String url = "http://120.79.229.78/zjj/myweb/public";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("6566", url + user.getThumb().replace("\\", "/"));
        Glide.with(getActivity()).load(url + MyApplication.user.getThumb().replace("\\", "/"))
                .dontAnimate()
                .bitmapTransform(new GlideCircleTransform(getActivity()))
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        thumb.setImageDrawable(resource);
                    }
                });
    }

    private void initView() {
        thumb = (ImageView) view.findViewById(R.id.thumb);
        image = (LinearLayout) view.findViewById(R.id.image);
        login_out = (LinearLayout) view.findViewById(R.id.login_out);
        Log.i("6565", url + user.getThumb().replace("\\", "/"));
        Glide.with(getActivity()).load(url + user.getThumb().replace("\\", "/"))
                .dontAnimate()
                .bitmapTransform(new GlideCircleTransform(getActivity()))
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        thumb.setImageDrawable(resource);
                    }
                });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_item);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        SettingAdapter homeAdapter = new SettingAdapter(contentList, getActivity());
        //添加分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(homeAdapter);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertView("上传头像", null, "取消", null,
                        new String[]{"拍照", "从相册中选择"}, getActivity(), AlertView.Style.ActionSheet, new OnItemClickListener() {
                    public void onItemClick(Object o, int position) {
                        switch (position) {
                            case 0:
                                //拍照获取图片
                                File outputImage = new File(getActivity().getExternalCacheDir(), "output_image.jpg");
                                try {
                                    if (outputImage.exists()) {
                                        outputImage.delete();
                                    }
                                    outputImage.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (Build.VERSION.SDK_INT >= 24) {
                                    imageUri = FileProvider.getUriForFile(getActivity(), "com.example.zjj20181218.icustody.fileprovider", outputImage);
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

        login_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertView("打扰了", "确定退出登录？", null, new String[]{"确定", "取消"}, null, getActivity(),
                        AlertView.Style.Alert, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        if (position == 0) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            ActivityCollector.finishAll();
                            startActivity(intent);
                        }
                    }
                }).setCancelable(true).show();
            }
        });
    }

    private void initData() {
        //初始化用户数据
        contentList.add(new Content("姓名", user.getName()));
        contentList.add(new Content("性别", user.getGender()));
        contentList.add(new Content("出生年月", user.getBirth()));
        contentList.add(new Content("邮箱", user.getEmail()));
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    try {
                        if (imageUri != null) {
                            Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                            //displayImage(getImagePath(imageUri, new String[]{MediaStore.Images.ImageColumns.DATA}, null));
                            displayImage(getFPUriToPath(getActivity(), imageUri));
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
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, selection, null, null);
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
            Intent intent = new Intent(getActivity(), ThumbActivity.class);
            intent.putExtra("imagePath", imagePath);
            startActivityForResult(intent, THUMB);
            //Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            //picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(getActivity(), "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    private String getPathByUri(Uri uri){
        String imagePath = null;
        if (DocumentsContract.isDocumentUri(getActivity(), uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
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
