package com.example.zjj20181218.icustody.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.zjj20181218.icustody.Activity.ImageViewActivity;
import com.example.zjj20181218.icustody.Activity.LBSActivity;
import com.example.zjj20181218.icustody.MyApplication;
import com.example.zjj20181218.icustody.R;
import com.example.zjj20181218.icustody.javaBean.InfoImage;
import com.example.zjj20181218.icustody.util.TimeUtil;

import java.util.List;


/**
 * Created by Zjj on 2018/12/28.
 */

public class InfoImageAdapter extends RecyclerView.Adapter<InfoImageAdapter.ViewHolder> {

    private String url = "http://120.79.229.78/zjj/myweb/public";
    private Context mContext;
    private List<InfoImage> mImageList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        LinearLayout linearLayout;
        ImageView imageView;
        TextView textView;
        TextView textView2;
        public View view;
        
        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            linearLayout = (LinearLayout) view.findViewById(R.id.line);
            imageView = (ImageView) view.findViewById(R.id.image_info);
            textView = (TextView) view.findViewById(R.id.name_info);
            textView2 = (TextView) view.findViewById(R.id.time_info);
        }
    }
    
    public InfoImageAdapter(List<InfoImage> infoImages){
        mImageList = infoImages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final InfoImage infoImage = mImageList.get(position);
        holder.textView.setText(infoImage.getLocation());//时间还未解析
        holder.textView2.setText(TimeUtil.stampToDate(infoImage.getTime()));//时间还未解析
        Glide.with(mContext).load(url + infoImage.getImg().replace("\\", "/"))
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
                intent.putExtra("image", url + infoImage.getImg());
                mContext.startActivity(intent);
            }
        });
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, LBSActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }
}
