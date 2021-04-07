package com.fxd.wangluo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.fxd.wangluo.utils.NetworkUtils;
import com.ss.bottomnavigation.BottomNavigation;
import com.ss.bottomnavigation.events.OnSelectedItemChangeListener;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActvity";
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_def_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));

        BottomNavigation bottomNavigation = (BottomNavigation) findViewById(R.id.bottom_navigation);
        bottomNavigation.setDefaultItem(0);
        bottomNavigation.setOnSelectedItemChangeListener(new OnSelectedItemChangeListener() {
            @Override
            public void onSelectedItemChanged(int itemId) {
                switch (itemId) {
                    case R.id.tab_home:
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_fragment_containers, new HomeFragment());
                        break;
                    case R.id.tab_images:
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_fragment_containers, new IncludeFragment());
                        break;
                    case R.id.tab_camera:
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_fragment_containers, new TypeFragment());
                        break;
                    case R.id.tab_products:
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_fragment_containers, new ChatFragment());
                        break;
                    case R.id.tab_more:
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_fragment_containers, new UserFragment());
                        break;
                }
                transaction.commit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 填充菜单; 如果存在，则会将项目添加到操作栏。
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setIconsVisible(menu, true);//利用反射，显示图标

//        MenuItem item1 = menu.add(0, 1, 0, "等待");
//        item1.setIcon(R.mipmap.ic_launch_1);//不在布局中添加icon
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //处理动作栏项点击此处。 动作栏会
        // 自动处理Home / Up按钮上的点击，这么久
        //在AndroidManifest.xml中指定父活动。
        switch (item.getItemId()) {
            case R.id.action_search:
                if (NetworkUtils.isConnected(this)) {
                    startActivity(new Intent(this, SearchActivity.class));
                    Log.d(TAG, "onViewCreated: 已经任何连接");
                } else {
                    Toast.makeText(this, "没网啊 怎么搜索呢", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onViewCreated: 没有任何连接");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //让图标显示的方法
    private void setIconsVisible(Menu menu, boolean flag) {
        //判断menu是否为空
        if (menu != null) {
            try {
                //如果不为空,就反射拿到menu的setOptionalIconsVisible方法
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                //暴力访问该方法
                method.setAccessible(true);
                //调用该方法显示icon
                method.invoke(menu, flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
