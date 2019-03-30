package com.example.zjj20181218.icustody;

import android.app.Application;
import android.content.Context;

import com.example.zjj20181218.icustody.javaBean.User;

/**
 * Created by Zjj on 2019/1/2.
 */

public class MyApplication extends Application {

    private static Context context;
    public static User user = null;

    @Override
    public void onCreate() {
        context = getApplicationContext();

    }

    public static Context getContext() {
        return context;
    }
}
