package com.fxd.wangluo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fxd.wangluo.SQLite.DataBaseHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.Manifest.permission.READ_CONTACTS;

// 通过电子邮件/密码提供登录的登录屏幕。
public class RegisterActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    //ID来标识READ_CONTACTS权限请求。
    private static final int REQUEST_READ_CONTACTS = 0;
    //跟踪登录任务，确保我们可以在需要时取消登录任务。
    private UserRegisterTask mAuthTask = null;

    //UI参考.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView, etUserName;
    private View mProgressView;
    private View mLoginFormView;
    private DataBaseHandler dataBaseHandler;

    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);//设置返回箭头显示
        actionBar.setTitle("注册");

        etUserName = (EditText) findViewById(R.id.register_username_et);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.register_email_actv);
        populateAutoComplete();
        mPasswordView = (EditText) findViewById(R.id.register_password_et);

        etUserName.addTextChangedListener(new GenericTextWatcher(etUserName));
        mEmailView.addTextChangedListener(new GenericTextWatcher(mEmailView));

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.register_login_btn);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress_pb);

        dataBaseHandler = new DataBaseHandler(this);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * 当权限请求已完成时收到回调。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * 尝试登录或注册登录表单指定的帐户。
     * 如果有表格错误（无效的电子邮件，缺少的字段等），那么
     * 出现错误，并且没有进行实际的登录尝试。
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        //重置错误。
        etUserName.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        //在登录尝试时存储值。
        String username = etUserName.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // 如果用户名，请检查一个有效的用户名。
        if (TextUtils.isEmpty(username)) {
            etUserName.setError(getString(R.string.error_field_required));
            focusView = etUserName;
            cancel = true;
        } else if (TextUtils.isEmpty(email)) {//检查一个有效的电子邮件地址。
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        } else if (password == "") {// 如果用户输入一个密码，请检查一个有效的密码。
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // 有一个错误; 不要尝试登录并将第一个表单集中在一个错误中。
            focusView.requestFocus();
        } else {
            //显示进度微调器，并启动后台任务来执行用户注册尝试。
            showProgress(true);
            mAuthTask = new UserRegisterTask(username, email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
// TODO：用你自己的逻辑代替它
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
// TODO：用你自己的逻辑代替它
        return password.length() > 4;
    }

    //显示进度UI并隐藏登记表。
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        //在Honeycomb MR2上，我们有ViewPropertyAnimator API，允许
        //非常简单的动画。 如果可用，请使用这些API淡入
        //进度微调器。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // ViewPropertyAnimator API不可用，所以只需显示
            //并隐藏相关的UI组件。
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                //检索设备用户'资料'联系人的数据行。
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                //只选择电子邮件地址。
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                //首先显示主要电子邮件地址。 请注意，不会有
                //如果用户未指定主电子邮件地址，则为主电子邮件地址。
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //创建适配器以告诉AutoCompleteTextView在其下拉列表中显示什么。
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private class GenericTextWatcher implements TextWatcher {

        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {
                case R.id.register_username_et:
                    Log.d("TAG", "afterTextChanged---------------register_username_et>" + text);
                    if (text == "") {
                        return;
                    }
                    RequestData(Constants.API.QUERY_REGISTER + "?a=quser&u=" + etUserName.getText().toString(), R.id.register_username_et);
                    break;
                case R.id.register_email_actv:
                    Log.d("TAG", "afterTextChanged---------------register_email_actv>" + text);
                    if (text == "") {
                        return;
                    }
                    RequestData(Constants.API.QUERY_REGISTER + "?a=qemail&e=" + text, R.id.register_email_actv);
                    break;
            }
        }
    }

    public void RequestData(String urls, final int id) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urls)
                .get().build();
        System.out.println("==============" + urls);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                //处理完成后给handler发送消息
                Message msg = new Message();
                switch (id) {
                    case R.id.register_username_et:
                        if (data.equals("none") || data.length() == 4) {
                            //用户名不存在 ，可用
                            System.out.println("==============用户名不存在 ，可用");
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }
                        if (data.equals("1") || data.length() == 1) {
                            //用户名存在 ，不可用
                            System.out.println("==============用户名存在 ，不可用");
                            msg.what = 2;
                            handler.sendMessage(msg);
                        }
                        Log.d("TAG", "afterTextChanged---------------register_username_et>");
                        break;
                    case R.id.register_email_actv:
                        if (data.equals("none") || data.length() == 4) {
                            //邮箱不存在 ，可用
                            System.out.println("==============邮箱不存在 ，可用");
                            msg.what = 3;
                            handler.sendMessage(msg);
                        }
                        if (data.equals("1") || data.length() == 1) {
                            //邮箱存在 ，不可用
                            System.out.println("==============邮箱存在 ，不可用");
                            msg.what = 4;
                            handler.sendMessage(msg);
                        }
                        Log.d("TAG", "afterTextChanged---------------register_email_actv>");
                        break;
                }
            }
        });
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            boolean cancel = false;
            View focusView = null;
            if (msg.what == 1) {//用户名不存在 ，可用
                etUserName.setError(getString(R.string.error_user_name_nothing));
                focusView = etUserName;
                etUserName.setError(null);
                cancel = true;
            }
            if (msg.what == 2) {//用户名存在 ，不可用
                etUserName.setError(getString(R.string.error_user_name_exist));
                focusView = etUserName;
                cancel = true;
            }
            if (msg.what == 3) {//邮箱不存在 ，可用
                mEmailView.setError(getString(R.string.error_email_nothing));
                focusView = mEmailView;
                mEmailView.setError(null);
                cancel = true;
            }
            if (msg.what == 4) {//邮箱存在 ，不可用
                mEmailView.setError(getString(R.string.error_email_exist));
                focusView = mEmailView;
                cancel = true;
            }
            if (cancel) {
                focusView.requestFocus();
            }
        }
    };

    /**
     * 表示用于认证的异步注册/注册任务
     * 用户。
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserName;
        private final String mEmail;
        private final String mPassword;
        private int id;

        UserRegisterTask(String username, String email, String password) {
            mUserName = username;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mAuthTask = null;
            showProgress(false);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(Constants.API.REGISTER_URL + "?username=" + mUserName + "&email=" + mEmail + "&password=" + mPassword)
                    .get().build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "网络请求失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    /**1.将参数+url，传给后台
                     *2.如成功，判断本地库是否有数据，有：清空表，跳转登录页面。无:跳转登录页面。
                     * 3.失败，返回，提示error message
                     */
                    final String data = response.body().string();
                    /**1.注册成功
                     * 2.先清空本地库，保证只有一条
                     * 3.跳转LoginActivity
                     */
                    System.out.println("false：" + data);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (data.equals("用户名已存在")) {
                                customAlert("用户名已存在");
                                etUserName.setError(getString(R.string.error_user_name_exist));
                                etUserName.requestFocus();
                                return;
                            }
                            if (data.equals("邮箱已使用")) {
                                customAlert("邮箱已使用");
                                mPasswordView.setError(getString(R.string.error_email_exist));
                                mPasswordView.requestFocus();
                                return;
                            }
                            if (data.equals("添加失败")) {
                                customAlert("注册失败 请重试");
                            } else {
//                                handler.deleteData();
//                                UserBean addData = new UserBean(id, netUserId, netHeadImg,
//                                        netName, netEmail, mPassword, netLabel);
//                                handler.addUserData(addData);
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            });
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
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

    public void customAlert(final String errorMessage) {
        alert = null;
        builder = new AlertDialog.Builder(RegisterActivity.this);
        alert = builder.setIcon(R.drawable.ic_chat)
                .setTitle("系统提示：")
                .setMessage(errorMessage)
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(RegisterActivity.this, "你点击了取消按钮~", Toast.LENGTH_SHORT).show();
                    }
                }).create();             //创建AlertDialog对象
        alert.show();                    //显示对话框
    }
}

