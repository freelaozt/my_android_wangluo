package com.fxd.wangluo;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.fxd.wangluo.Adapter.LabelAdapter;
import com.fxd.wangluo.Bean.LabelBean;
import com.fxd.wangluo.View.LabelInfoActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author S.Shahini
 * @since 10/19/16
 */

public class TypeFragment extends Fragment implements OnRefreshListener {
    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView rvSwipeTarget;
    private List<LabelBean> beanList;
    private LabelAdapter mLabelAdapter;
    private static final String TAG = "TypeFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLabelAdapter = new LabelAdapter(3);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_type, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeToLoadLayout = (SwipeToLoadLayout) view.findViewById(R.id.swipeToLoadLayout);
        rvSwipeTarget = (RecyclerView) view.findViewById(R.id.swipe_target);

        beanList = new ArrayList<LabelBean>();

        //加布局管理
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
        rvSwipeTarget.setLayoutManager(staggeredGridLayoutManager);

        swipeToLoadLayout.setOnRefreshListener(this);

//        rvSwipeTarget.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    if (!ViewCompat.canScrollVertically(recyclerView, 1)) {
//                        swipeToLoadLayout.setLoadingMore(true);
//                    }
//                }
//            }
//        });

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
    }

    @Override
    public void onPause() {
        super.onPause();
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
        if (swipeToLoadLayout.isLoadingMore()) {
            swipeToLoadLayout.setLoadingMore(false);
        }
    }

    @Override
    public void onRefresh() {
        swipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                String url = Constants.API.QUERY_ALL_LABEL + "?a=qLabel";
                beanList = null;
                requestData(url);
                swipeToLoadLayout.setRefreshing(false);
            }
        }, 1000);
    }

    public void requestData(String URL) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "网络请求失败", Toast.LENGTH_SHORT).show();
                        //本地解析JSON文件
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                if (getActivity() != null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (json.equals("none") || json.length() == 4) {
                                return;
                            }
                            gsonJson(json);

                            mLabelAdapter.setList(getActivity(), beanList);
                            rvSwipeTarget.setAdapter(mLabelAdapter);

                            mLabelAdapter.setmOnItemClickListener(new LabelAdapter.OnRVItemClickListener() {
                                @Override
                                public void onItemClick(View view) {
                                    int i = rvSwipeTarget.getChildAdapterPosition(view);
                                    Intent intent = new Intent(getActivity(), LabelInfoActivity.class);
                                    intent.putExtra("keyLabel", beanList.get(i).getLabelTitle());
                                    startActivity(intent);
                                }

                                @Override
                                public void onItemLongClick(View view) {

                                }
                            });
                        }
                    });
                }else {
                    return;
                }

            }
        });
    }

    public void gsonJson(String json) {
        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject(json);
            String jsonData = jsonObject.getString("data");
            if (beanList == null || beanList.size() == 0) {
                beanList = gson.fromJson(jsonData, new TypeToken<List<LabelBean>>() {
                }.getType());
            } else {
                List<LabelBean> more = gson.fromJson(jsonData, new TypeToken<List<LabelBean>>() {
                }.getType());
                beanList.addAll(more);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        //获得assets资源管理器
        AssetManager assetManager = context.getAssets();
        //使用IO流读取json文件内容
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName), "utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
