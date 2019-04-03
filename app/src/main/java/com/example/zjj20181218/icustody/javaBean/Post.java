package com.example.zjj20181218.icustody.javaBean;

import java.util.List;

/**
 * Created by Jaeger on 16/2/24.
 *
 * Email: chjie.jaeger@gmail.com
 * GitHub: https://github.com/laobie
 */
public class Post {
    private int id;
    private String mContent;
    private String mTime;
    private int mSpanType;
    private List<String> mImgUrlList;

    public Post() {
    }

    public Post(String content, List<String> imgUrlList, String time) {
        mContent = content;
        mImgUrlList = imgUrlList;
        mTime = time;
    }
    public Post(String content,int spanType, List<String> imgUrlList) {
        mContent = content;
        mSpanType = spanType;
        mImgUrlList = imgUrlList;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        this.mTime = time;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public int getmSpanType() {
        return mSpanType;
    }

    public void setmSpanType(int mSpanType) {
        this.mSpanType = mSpanType;
    }

    public List<String> getImgUrlList() {
        return mImgUrlList;
    }

    public void setImgUrlList(List<String> imgUrlList) {
        mImgUrlList = imgUrlList;
    }
}
