package com.fxd.wangluo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class SearchActivity extends AppCompatActivity implements TextView.OnEditorActionListener,  TextWatcher, SearchBeforeFragment.MyListener {
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);//设置返回箭头显示
        actionBar.setTitle("");

        etSearch = (EditText) findViewById(R.id.search_et);
        String myHexColor = "#DDDDDD";
        etSearch.setHintTextColor(Color.parseColor(myHexColor));

        etSearch.setOnEditorActionListener(this);

        etSearch.addTextChangedListener(this);

        setDefaultFragment("1");
    }

    @Override
    public void sendContent(String info) {
        etSearch.setText(info);
        etSearch.setSelection(info.length());//将光标移至文字末尾
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d("TAG","beforeTextChanged--------------->");
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d("TAG","onTextChanged--------------->");
    }

    @Override
    public void afterTextChanged(Editable editable) {
        Log.d("TAG","afterTextChanged--------------->");
        String key = editable.toString();
        if (key == "") {
            return;
        }
        setDefaultFragment(key);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            //传参数 切换fragment
            setSecondFragment();
            //搜索完，隐藏软键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            handled = true;
        }
        return handled;
    }


    //设置默认的
    private void setDefaultFragment(String k) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.layFrame, SearchBeforeFragment.newInstance(k));
        transaction.commit();
    }

    //设置默认的
    private void setSecondFragment() {
        String keyWord = etSearch.getText().toString();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.layFrame, SearchAfterFragment.newInstance(keyWord));
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //点击返回箭头结束activity
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
