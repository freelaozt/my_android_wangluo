package com.fxd.wangluo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Search2Activity extends AppCompatActivity implements TextView.OnEditorActionListener,  TextWatcher, ClankFragment.MyListener {
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);

        etSearch = (EditText) findViewById(R.id.search_et);

        etSearch.setOnEditorActionListener(this);

        etSearch.addTextChangedListener(this);

        setDefaultFragment();

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
        setDefaultFragment();
    }

    @Override
    public void afterTextChanged(Editable editable) {
        Log.d("TAG","afterTextChanged--------------->");
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
            Toast.makeText(this, "点击actionSearch执行的操作 ", Toast.LENGTH_SHORT).show();
            handled = true;
        }
        return handled;
    }


    //设置默认的
    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.layFrame, ClankFragment.newInstance("我是 热门标签"));
        transaction.commit();
    }

    //设置默认的
    private void setSecondFragment() {
        String keyWord = etSearch.getText().toString();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.layFrame, BlankFragment.newInstance(keyWord));
        transaction.commit();
    }

}
