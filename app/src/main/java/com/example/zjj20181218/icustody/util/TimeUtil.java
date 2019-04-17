package com.example.zjj20181218.icustody.util;

import android.icu.text.SimpleDateFormat;
import android.net.ParseException;
import android.os.Build;

import java.util.Date;

public class TimeUtil {
    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        Date date = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            try {
                date = simpleDateFormat.parse(s);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res = null;
        SimpleDateFormat simpleDateFormat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        long lt = new Long(s);
        Date date = new Date(lt);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            res = simpleDateFormat.format(date);
        }
        return res;
    }
}
