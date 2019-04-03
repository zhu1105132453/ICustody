package com.example.zjj20181218.icustody.Activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.example.zjj20181218.icustody.Fragment.DiscoverFragment;
import com.example.zjj20181218.icustody.Fragment.HomeFragment;
import com.example.zjj20181218.icustody.Fragment.SectionsPagerAdapter;
import com.example.zjj20181218.icustody.Fragment.SettingFragment;
import com.example.zjj20181218.icustody.R;
import com.example.zjj20181218.icustody.util.PermissionUtil;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Zjj on 2018/12/19.
 */

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener, ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private BottomNavigationBar bn;
    private List<Fragment> fragments;

    private AlertView mAlertView;//避免创建重复View，先创建View，然后需要的时候show出来，推荐这个做法

    private FloatingActionButton rightLowerButton;
    private FloatingActionMenu rightLowerMenu;
    private ImageView fabIconNew;

    //权限
    String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initPermission();
        initBottomNavigationBar();
        initViewPager();
    }

    private void initBottomNavigationBar() {
        bn = (BottomNavigationBar) findViewById(R.id.bnv_bottom_activity);
        //添加Item选项
        bn.setTabSelectedListener(this);
        bn.clearAll();
        bn.setMode(BottomNavigationBar.MODE_FIXED);
        bn.addItem(new BottomNavigationItem(R.drawable.icon_main_home_selected, "主页")
                .setInactiveIconResource(R.drawable.icon_main_home_normal)
                .setActiveColorResource(R.color.orange))
                .addItem(new BottomNavigationItem(R.drawable.icon_main_category_selected, "动态")
                        .setInactiveIconResource(R.drawable.icon_main_category_normal)
                        .setActiveColorResource(R.color.orange))
                .addItem(new BottomNavigationItem(R.drawable.icon_main_mine_selected, "设置")
                        .setInactiveIconResource(R.drawable.icon_main_mine_normal)
                        .setActiveColorResource(R.color.orange))
                .initialise();
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new DiscoverFragment());
        fragments.add(new SettingFragment());

        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.addOnPageChangeListener(this);
        viewPager.setCurrentItem(0);

        initView();
    }

    private void initPermission() {
        PermissionUtil.getInstance().chekPermissions(this, permissions, permissionsResult);
    }

    @Override
    public void onTabSelected(int position) {//未选中->选中
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onTabUnselected(int position) {//选中->未选中
    }

    @Override
    public void onTabReselected(int position) {//选中->选中
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        bn.selectTab(position);
        if (position == 1) {
            rightLowerButton.setVisibility(View.VISIBLE);
        } else {
            rightLowerMenu.close(true);
            rightLowerButton.setVisibility(View.GONE);//隐藏
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private PermissionUtil.IPermissionsResult permissionsResult = new PermissionUtil.IPermissionsResult() {
        @Override
        public void passPermissons() {
            Toast.makeText(MainActivity.this, "权限通过", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void forbitPermissons() {
            Toast.makeText(MainActivity.this, "权限不通过！请打开设置开启蓝牙权限", Toast.LENGTH_SHORT).show();
        }
    };

    private void initView() {

        mAlertView = new AlertView("喜欢就赞赏下吧？", "假装有张图片", null, new String[]{}, null, MainActivity.this,
                AlertView.Style.Alert, null);

        fabIconNew = new ImageView(this);

        // 设置菜单按钮Button的图标
        fabIconNew.setImageResource(R.drawable.add);
        rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_CENTER)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);

        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);
        ImageView rlIcon4 = new ImageView(this);

        rightLowerButton.setVisibility(View.GONE);//隐藏

        // 设置弹出菜单的图标
        rlIcon1.setImageResource(R.drawable.share);
        rlIcon2.setImageResource(R.drawable.location);
        rlIcon3.setImageResource(R.drawable.photo);
        rlIcon4.setImageResource(R.drawable.shuoshuo);

        rightLowerMenu = new FloatingActionMenu.Builder(
                this)
                .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon3).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon4).build())
                .attachTo(rightLowerButton)
                .setStartAngle(-45)
                .setEndAngle(-135)
                .build();

        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {

            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // 逆时针旋转90°
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(
                        View.ROTATION, -90);

                ObjectAnimator animation = ObjectAnimator
                        .ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // 顺时针旋转90°
                Log.i("asas", "???");
                fabIconNew.setRotation(-90);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(
                        View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator
                        .ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();

            }
        });

        rlIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rightLowerMenu.close(true);
                mAlertView.setCancelable(true);
                mAlertView.show();
            }
        });

        rlIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rightLowerMenu.close(true);
                Intent intent = new Intent(MainActivity.this, LBSActivity.class);
                startActivity(intent);
            }
        });

        rlIcon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rightLowerMenu.close(true);
                Intent intent = new Intent(MainActivity.this, TalkActivity.class);
                intent.putExtra("title", "photo");
                startActivity(intent);
            }
        });

        rlIcon4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rightLowerMenu.close(true);
                Intent intent = new Intent(MainActivity.this, TalkActivity.class);
                intent.putExtra("title", "talk");
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mAlertView != null && mAlertView.isShowing()) {
                mAlertView.dismiss();
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);

    }

    private static boolean mBackKeyPressed = false;//记录是否有首次按键

    @Override
    public void onBackPressed() {
        if (!mBackKeyPressed) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mBackKeyPressed = true;
            new Timer().schedule(new TimerTask() {//延时两秒，如果超出则擦错第一次按键记录
                @Override
                public void run() {
                    mBackKeyPressed = false;
                }
            }, 2000);
        } else {//退出程序
            ActivityCollector.finishAll();
        }
    }

}
