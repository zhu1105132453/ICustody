package com.example.zjj20181218.icustody.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Zjj on 2018/12/18.
 */

public class BaseActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
    
}
