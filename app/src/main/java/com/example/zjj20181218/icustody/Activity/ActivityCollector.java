package com.example.zjj20181218.icustody.Activity;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zjj on 2018/12/18.
 */

public class ActivityCollector {
    
    public static List<Activity> activities = new ArrayList<>();
    
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    
    public static void removeActivity(Activity activity){
        activities.remove(activity);
        activity.finish();
    }
    
    public static void finishAll(){
        for (Activity activity : activities){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
        activities.clear();
    }
}
