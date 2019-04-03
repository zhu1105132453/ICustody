package com.example.zjj20181218.icustody.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.zjj20181218.icustody.Activity.ImageViewActivity;
import com.example.zjj20181218.icustody.Activity.LBSActivity;
import com.example.zjj20181218.icustody.R;

import java.util.List;

public class TalkAdapter extends RecyclerView.Adapter<TalkAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mImageList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image_talk);
           /* FrameLayout.LayoutParams params= (FrameLayout.LayoutParams) imageView.getLayoutParams();
            //获取当前控件的布局对象
            params.height = 100;//设置当前控件布局的高度
            imageView.setLayoutParams(params);//将设置好的布局参数应用到控件中*/
        }
    }

    public TalkAdapter(List<String> images){
        mImageList = images;
    }

    @Override
    public TalkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_talk, parent, false);
        return new TalkAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TalkAdapter.ViewHolder holder, final int position) {
        final String image = mImageList.get(position);

        Glide.with(mContext).load(image)
                .dontAnimate()
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        holder.imageView.setImageDrawable(resource);
                    }
                });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImageViewActivity.class);
                intent.putExtra("image", image);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }
}
