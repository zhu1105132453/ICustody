package com.example.zjj20181218.icustody.util.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class MyLayout extends RelativeLayout {

    public  MyLayout  (Context context, AttributeSet attrs,
                       int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyLayout (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLayout (Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));

        int childWidthSize = getMeasuredWidth();
        // 高度和宽度一样
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}