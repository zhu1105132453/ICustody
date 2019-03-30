package com.example.zjj20181218.icustody.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.zjj20181218.icustody.MyApplication;
import com.example.zjj20181218.icustody.R;

/**
 * Created by Zjj on 2019/1/4.
 */

public class LBSActivity extends BaseActivity {
    
    public LocationClient mLocationClient;
    private MapView mapView;
    
    private BaiduMap baiduMap;
    
    private boolean isFirstLocate = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(MyApplication.getContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(MyApplication.getContext());
        setContentView(R.layout.activity_lbs);
        
        init();
    }

    private void init() {
        mapView = (MapView) findViewById(R.id.bmapView); 
        baiduMap = mapView.getMap();
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(16f));
        baiduMap.setMyLocationEnabled(true);//显示光标

        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }
    
    private void navigateTo(BDLocation location){
        if (isFirstLocate){
            Toast.makeText(this, "nav to " + location.getAddrStr(), Toast.LENGTH_SHORT).show();
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder.latitude(location.getLatitude());
        builder.longitude(location.getLongitude());
        MyLocationData locationData = builder.build();
        baiduMap.setMyLocationData(locationData);
        
    }
    
    public class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                navigateTo(bdLocation);
            }
        }
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }
}
