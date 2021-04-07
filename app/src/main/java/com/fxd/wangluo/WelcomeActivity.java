package com.fxd.wangluo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WelcomeActivity extends Activity {

    private int versionCode;
    private String versionName;
    private static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        try {
            versionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
            versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(2000);

                    Log.d(TAG, versionCode + "onCreate: " + versionName);

                    String URL = Constants.API.QUERY + "?a=qVersion&v=" + versionName;
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(URL)
                            .get().build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            final String json = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (json.equals("on")) {
                                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                    if (json.equals("off")) {
                                        Toast.makeText(getApplicationContext(), "版本需要更新<", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.setAction("android.intent.action.VIEW");
                                        Uri content_url = Uri.parse("https://www.nizwop.com");
                                        intent.setData(content_url);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                    if (json.equals("none")) {
                                        Toast.makeText(getApplicationContext(), "错误", Toast.LENGTH_SHORT).show();
                                        finish();
                                        return;
                                    }
                                }
                            });
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
