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
        // ????????????; ???????????????????????????????????????????????????
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setIconsVisible(menu, true);//???????????????????????????

//        MenuItem item1 = menu.add(0, 1, 0, "??????");
//        item1.setIcon(R.mipmap.ic_launch_1);//?????????????????????icon
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //????????????????????????????????? ????????????
        // ????????????Home / Up??????????????????????????????
        //???AndroidManifest.xml?????????????????????
        switch (item.getItemId()) {
            case R.id.action_search:
                if (NetworkUtils.isConnected(this)) {
                    startActivity(new Intent(this, SearchActivity.class));
                    Log.d(TAG, "onViewCreated: ??????????????????");
                } else {
                    Toast.makeText(this, "????????? ???????????????", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onViewCreated: ??????????????????");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //????????????????????????
    private void setIconsVisible(Menu menu, boolean flag) {
        //??????menu????????????
        if (menu != null) {
            try {
                //???????????????,???????????????menu???setOptionalIconsVisible??????
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                //?????????????????????
                method.setAccessible(true);
                //?????????????????????icon
                method.invoke(menu, flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
