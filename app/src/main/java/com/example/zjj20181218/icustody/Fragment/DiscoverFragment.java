package com.example.zjj20181218.icustody.Fragment;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.zjj20181218.icustody.R;
import com.example.zjj20181218.icustody.adapter.PostAdapter;
import com.example.zjj20181218.icustody.javaBean.Post;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by maxliaops on 17-1-6.
 */

public class DiscoverFragment extends Fragment {

    private RecyclerView mRvPostLister;
    private PostAdapter mNineImageAdapter;

    private List<Post> mPostList;
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
        rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        initView();
        return rootView;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initData();
        }
    }

    private void initView() {
        mRvPostLister = (RecyclerView) rootView.findViewById(R.id.talk);
    }

    private void initData() {
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRvPostLister.setLayoutManager(manager);
        mPostList = new ArrayList<>();
        //联网获取说说信息
        for (int i = 0; i < 9; i++) {
            List<String> imgUrls = new ArrayList<>();
            imgUrls.addAll(Arrays.asList(IMG_URL_LIST).subList(0, (i + 1) % 10));
            Post post = new Post("看看", imgUrls);
            mPostList.add(post);
        }

        mNineImageAdapter = new PostAdapter(getActivity(), mPostList, NineGridImageView.STYLE_GRID);
        mRvPostLister.setAdapter(mNineImageAdapter);

        manager.scrollToPositionWithOffset(0, 0);
        mRvPostLister.post(new Runnable() {
            @Override
            public void run() {
                View view = manager.findViewByPosition(1);
                if (view != null) System.out.println(view.getMeasuredHeight());
            }
        });
    }


}
