package com.example.zjj20181218.icustody.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.zjj20181218.icustody.OnDialogViewListener;
import com.example.zjj20181218.icustody.R;

/**
 * Created by Zjj on 2018/12/19.
 */

public abstract class DialogUtil extends Dialog {

    private Context context;
    private int layoutId;
    private OnDialogViewListener onDialogViewListener;

    protected abstract void onTouchOutside();

    public DialogUtil(Context context) {
        super(context);
    }

    public DialogUtil(Context context, int themeResId, int layoutId) {
        super(context, themeResId);
        this.context = context;
        this.layoutId = layoutId;
    }

    public void setOnDialogViewListener(OnDialogViewListener onDialogViewListener) {
        this.onDialogViewListener = onDialogViewListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View layout = inflater.inflate(layoutId, null);
        this.setContentView(layout);
        Window window = getWindow();
        //设置边框距离
        window.getDecorView().setPadding(0, 0, 0, 0);
        //设置dialog位置
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        onDialogViewListener.onDialogViewListener();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (isOutBounds(getContext(), event)) {
            onTouchOutside();
        }
        return super.onTouchEvent(event);
    }

    private boolean isOutBounds(Context context, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        final View decorView = getWindow().getDecorView();
        return ((x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop)) || (y > (decorView.getHeight()) + slop));
    }
}
