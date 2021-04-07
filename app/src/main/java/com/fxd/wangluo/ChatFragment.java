package com.fxd.wangluo;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.fxd.wangluo.utils.NetworkUtils;

/**
 * @author S.Shahini
 * @since 10/19/16
 */

public class ChatFragment extends Fragment implements OnRefreshListener, OnLoadMoreListener {
    private SwipeToLoadLayout swipeToLoadLayout;
    private WebView webView;
    private String URL = "http://www.nizwop.com:3000";
    private static final String APP_CACHE_DIRNAME = "/webcache"; // web缓存目录
    private Context context;
    private static final String TAG = "ChatFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        if (NetworkUtils.isWifiConnection(context)) {
            Log.d(TAG, "onViewCreated: 链接WiFi");
        } else {
            Log.d(TAG, "onViewCreated: 没有连接WiFi");
        }

        if (NetworkUtils.isConnected(context)) {
            Log.d(TAG, "onViewCreated: 已经任何连接");
        } else {
            Log.d(TAG, "onViewCreated: 没有任何连接");
        }

        if (NetworkUtils.isConnectionFast(context)) {
            Log.d(TAG, "onViewCreated: 已经快速连接网络");
        } else {
            Log.d(TAG, "onViewCreated: 快速断网");
        }

        if (NetworkUtils.isMobileConnection(context)) {
            Log.d(TAG, "onViewCreated: 已经连接手机网络");
        } else {
            Log.d(TAG, "onViewCreated: 断开手机网络");
        }
        swipeToLoadLayout = (SwipeToLoadLayout) view.findViewById(R.id.swipeToLoadLayout);
        webView = (WebView) view.findViewById(R.id.swipe_target);

        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeToLoadLayout.setRefreshing(false);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                swipeToLoadLayout.setRefreshing(false);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                swipeToLoadLayout.setRefreshing(false);
            }
        });


        //设置缓存模式
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 开启DOM storage API 功能
        settings.setDomStorageEnabled(true);
        // 开启database storage API功能
        settings.setDatabaseEnabled(true);
        String cacheDirPath = getActivity().getFilesDir().getAbsolutePath() + APP_CACHE_DIRNAME;
        Log.d(TAG, "cachePath: " + cacheDirPath);
        // 设置数据库缓存路径
        settings.setAppCachePath(cacheDirPath);
        settings.setAppCacheEnabled(true);
//        webView.clearCache(true);
//        webView.reload();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeToLoadLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }


    @Override
    public void onLoadMore() {
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setLoadingMore(false);
            }
        }, 2000);
    }

    @Override
    public void onRefresh() {
        webView.loadUrl(URL);
    }
}
