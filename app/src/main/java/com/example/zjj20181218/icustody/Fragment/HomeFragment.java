package com.example.zjj20181218.icustody.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zjj20181218.icustody.MyApplication;
import com.example.zjj20181218.icustody.adapter.InfoImageAdapter;
import com.example.zjj20181218.icustody.R;
import com.example.zjj20181218.icustody.javaBean.InfoImage;
import com.example.zjj20181218.icustody.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private InfoImageAdapter infoImageAdapter;

    private List<InfoImage> imageList = new ArrayList<>();
    private View rootView;
    private Activity mActivity = getActivity();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        return rootView;
    }

    private void initView() {

        recyclerView = (RecyclerView) rootView.findViewById(R.id.list_item);
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 1);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            initData();
        }
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = HttpUtil.post("http://120.79.229.78/zjj/android/database/info.php", "userid", MyApplication.user.getId());
                    if (response != null) {
                        String result = response.body().string().replace("\\","/");
                        Gson gson = new Gson();
                        imageList = gson.fromJson(result, new TypeToken<List<InfoImage>>(){}.getType());

                        infoImageAdapter = new InfoImageAdapter(imageList);
                        Log.i("asasa", imageList.size() + "");

                        Message message = new Message();
                        message.what = 10086;
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 10086:
                    recyclerView.setAdapter(infoImageAdapter);
                    break;
                default:
                    break;
            }
        }
    };

}
