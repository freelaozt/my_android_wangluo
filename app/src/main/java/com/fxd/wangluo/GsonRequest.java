package com.fxd.wangluo;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

/**
 * GSON处理
 */
public class GsonRequest<T> extends Request<T> {
    private Response.Listener<T> mListener;
    private Class<T> mClazz;
    private Gson mGson;
    private static final String PROTOCOL_CHARSET = "utf-8";

    public GsonRequest(String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, clazz, listener, errorListener);
    }

    public GsonRequest(int method, String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mClazz = clazz;
        this.mGson = new Gson();
    }

    public GsonRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String parsed = null;
        try {
//            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            parsed = new String(response.data, PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException e) {
//            parsed = new String(response.data, PROTOCOL_CHARSET);
        }
        T t = mGson.fromJson(parsed, mClazz);
        //TODO
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Response.success(t, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(T t) {
        mListener.onResponse(t);
    }
}
