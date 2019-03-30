package com.example.zjj20181218.icustody.Activity;

import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.zjj20181218.icustody.R;

import java.io.File;

/**
 * Created by Zjj on 2019/1/4.
 */

public class ImageViewActivity extends BaseActivity {

    private SubsamplingScaleImageView imageView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);

        String imageUrl = getIntent().getStringExtra("image");

        imageView = (SubsamplingScaleImageView) findViewById(R.id.photo);
        //设置图片openGl的最大值
        imageView.setMaxTileSize(2730,Integer.MAX_VALUE);
        Log.i("aad", imageUrl);

        Glide.with(ImageViewActivity.this)
                .load(imageUrl)
                .downloadOnly(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                        imageView.setImage(ImageSource.uri(resource.getAbsolutePath()),
                                new ImageViewState(1.0f, new PointF(0, 0), 0));
                    }
                });
                    
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        imageView.recycle();
        ActivityCollector.removeActivity(this);
    }
}
