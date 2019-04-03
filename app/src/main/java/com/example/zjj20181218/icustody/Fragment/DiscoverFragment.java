package com.example.zjj20181218.icustody.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zjj20181218.icustody.R;
import com.example.zjj20181218.icustody.adapter.PostAdapter;
import com.example.zjj20181218.icustody.javaBean.Post;
import com.example.zjj20181218.icustody.javaBean.Talk;
import com.example.zjj20181218.icustody.util.HttpUtil;
import com.example.zjj20181218.icustody.util.db.TalkDao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaeger.ninegridimageview.NineGridImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/**
 * Created by maxliaops on 17-1-6.
 */

public class DiscoverFragment extends Fragment {

    private static final int NET = 111;
    private static final int NET_FAILURE = 112;
    private static final int LOCAL = 222;

    private RecyclerView mRvPostLister;
    private PostAdapter mNineImageAdapter;
    private LinearLayoutManager manager;
    private SwipeRefreshLayout swipeRefresh;

    private List<Post> mPostList = new ArrayList<>();
    private View rootView;
    private String[] IMG_URL_LIST = {
            "https://pic4.zhimg.com/02685b7a5f2d8cbf74e1fd1ae61d563b_xll.jpg",
            "https://pic4.zhimg.com/fc04224598878080115ba387846eabc3_xll.jpg",
            "https://pic3.zhimg.com/d1750bd47b514ad62af9497bbe5bb17e_xll.jpg",
            "https://pic4.zhimg.com/da52c865cb6a472c3624a78490d9a3b7_xll.jpg",
            "https://pic3.zhimg.com/0c149770fc2e16f4a89e6fc479272946_xll.jpg",
            "https://pic1.zhimg.com/76903410e4831571e19a10f39717988c_xll.png",
            "https://pic3.zhimg.com/33c6cf59163b3f17ca0c091a5c0d9272_xll.jpg",
            "https://pic4.zhimg.com/52e093cbf96fd0d027136baf9b5cdcb3_xll.png",
            "https://pic3.zhimg.com/f6dc1c1cecd7ba8f4c61c7c31847773e_xll.jpg",
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_discover, container, false);
        initView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onresme", "test");
        loadByLocal();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initData();
        }
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        mRvPostLister = (RecyclerView) rootView.findViewById(R.id.talk);
        manager = new LinearLayoutManager(getActivity());
        mRvPostLister.setLayoutManager(manager);
        mNineImageAdapter = new PostAdapter(getActivity(), mPostList, NineGridImageView.STYLE_GRID);
        swipeRefresh =(SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
        swipeRefresh.setColorSchemeColors(R.color.blue);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TalkDao talkDao = new TalkDao(getContext());
                if (talkDao.selectAll().size() > 0) {
                    talkDao.clearTableData();
                    loadByNet();
                }
            }
        });
    }

    private void initData() {
        //联网获取说说信息
        //先从本地获取，如果本地没有则联网获取
        loadByLocal();
    }

    private void loadByLocal() {
        TalkDao talkDao = new TalkDao(getContext());
        List<Talk> talks = talkDao.selectAll();
        if (talks.size() > 0){
            for (Talk talk : talks) {
                String arr[] = talk.getImg().split("\\|");
                List<String> imgUrl = new ArrayList<>();
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = "http://120.79.229.78/zjj/myweb/public" + arr[i];
                    imgUrl.add(arr[i]);
                    Log.i("img", imgUrl.get(i));
                }
                Post post = new Post(talk.getContent(), imgUrl, talk.getTime());
                mPostList.add(post);
                mRvPostLister.setAdapter(mNineImageAdapter);
                manager.scrollToPositionWithOffset(0, 0);
                swipeRefresh.setRefreshing(false);
            }
        } else {
            loadByNet();
        }
    }

    private void loadByNet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = HttpUtil.get("http://120.79.229.78/zjj/android/database/imgUrl.php");
                    String result = response.body().string().replace("\\\\","/");
                    if (!result.equals("")) {
                        Log.d("r", result);
                        mPostList.clear();
                        Gson gson = new Gson();
                        List<Talk> talkList = gson.fromJson(result, new TypeToken<List<Talk>>() {
                        }.getType());
                        //加载图片并保存值本地
                        TalkDao talkDao = new TalkDao(getContext());
                        for (Talk talk : talkList) {
                            //存储到本地数据库
                            talkDao.insert(talk);

                            Log.i("img", talk.getImg());
                            String arr[] = talk.getImg().split("\\|");
                            List<String> imgUrl = new ArrayList<>();
                            for (int i = 0; i < arr.length; i++) {
                                arr[i] = "http://120.79.229.78/zjj/myweb/public" + arr[i];
                                imgUrl.add(arr[i]);
                                Log.i("img", imgUrl.get(i));
                            }
                            Post post = new Post(talk.getContent(), imgUrl, talk.getTime());
                            mPostList.add(post);
                        }
                        Message message = new Message();
                        message.what = NET;
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = NET_FAILURE;
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
                case NET:
                    mRvPostLister.setAdapter(mNineImageAdapter);
                    manager.scrollToPositionWithOffset(0, 0);
                    swipeRefresh.setRefreshing(false);
                    break;
                case NET_FAILURE:
                    mRvPostLister.setAdapter(mNineImageAdapter);
                    manager.scrollToPositionWithOffset(0, 0);
                    swipeRefresh.setRefreshing(false);
                    Toast.makeText(getActivity(), "加载失败！", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

}
