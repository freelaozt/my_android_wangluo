package com.fxd.wangluo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fxd.wangluo.Adapter.LabelAdapter;
import com.fxd.wangluo.Adapter.SearchItemAdapter;
import com.fxd.wangluo.Bean.LabelBean;
import com.fxd.wangluo.Bean.SearchBean;
import com.fxd.wangluo.View.LabelInfoActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.library.flowlayout.FlowLayoutManager;
import com.library.flowlayout.SpaceItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchBeforeFragment extends Fragment {
    private static final String ARG_PARAM2 = "param2";
    private String mParam2;
    private RecyclerView rvSwipeTarget, rvHotSwipeTarget;
    private TextView fragmentTv2;
    private CardView cvHotLabel;
    private List<SearchBean> searchBeenList;
    private List<LabelBean> labelBeenList;
    private SearchItemAdapter mAdapter;
    private LabelAdapter labelAdapter;
    private LinearLayoutManager linearManager;
    private Request request;

    private LinearLayout mLinearAddView;
    private List<String> titleList;
//    private String orStr = "DVD;网盘;1080P;桥本奈奈美;";

    private static final String TAG = "SearchBeforeFragment";

    private MyListener myListener;//②作为属性定义

    //①定义回调接口
    public interface MyListener {
        public void sendContent(String info);
    }

    public static SearchBeforeFragment newInstance(String param2) {
        SearchBeforeFragment fragment = new SearchBeforeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam2 = getArguments().getString(ARG_PARAM2);
            Log.d("onCreate: ", "mParam2: " + mParam2);
            labelAdapter = new LabelAdapter(4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_before, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        Log.d(TAG, "onViewCreated: " + mParam2.length());
        if (mParam2.equals("1") || mParam2.length() == 0) {
            //显示热门 不显示搜索结果
            rvSwipeTarget.setVisibility(View.GONE);

            fragmentTv2.setText("热门搜索");

            String url = Constants.API.QUERY_ALL_LABEL + "?a=qLabel";
            hotRequestData(url);

//            mLinearAddView = (LinearLayout) view.findViewById(R.id.linear_add_view);
//            titleList = new ArrayList<>();
//            initData();

        } else {
            //显示搜索结果 不显示热门
            cvHotLabel.setVisibility(View.GONE);

            String URL = Constants.API.SEARCH_KEYWORD + mParam2 + "&p=0";
            searchBeenList = null; //再次请求时，清空数据源,
            searchRequestData(URL);
        }
    }

    public void initView(View v) {
        cvHotLabel = (CardView) v.findViewById(R.id.cv_label);
        fragmentTv2 = (TextView) v.findViewById(R.id.fragment_tv2);
        rvHotSwipeTarget = (RecyclerView) v.findViewById(R.id.rv_swipe_target);
        labelBeenList = new ArrayList<LabelBean>();
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager();
        //设置每一个item间距
        rvHotSwipeTarget.addItemDecoration(new SpaceItemDecoration(dp2px(2)));
        rvHotSwipeTarget.setLayoutManager(flowLayoutManager);
//        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
//        rvHotSwipeTarget.setLayoutManager(staggeredGridLayoutManager);

        rvSwipeTarget = (RecyclerView) v.findViewById(R.id.swipe_target);
        searchBeenList = new ArrayList<SearchBean>();
        //加布局管理
        linearManager = new LinearLayoutManager(getActivity());
        linearManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearManager.setStackFromEnd(true);
        rvSwipeTarget.setLayoutManager(linearManager);
    }

    public void searchRequestData(String URL) {
        OkHttpClient client = new OkHttpClient();
        request = new Request.Builder()
                .url(URL)
                .get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "网络请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String regEx = "[\"`~!@#$%^&*()+=|{}';',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                            Pattern p = Pattern.compile(regEx);
                            Matcher m = p.matcher(data);
                            String s = m.replaceAll("").trim();
                            Log.d(TAG, "onViewCreated: " + s.length());
                            if (s.equals("null") || s.equals("none") || s.length() == 4 || s.length() == 0) {
                                Toast.makeText(getActivity(), "没有相关数据", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                gsonParseJson(data);
                            }

                            mAdapter = new SearchItemAdapter(getActivity(), searchBeenList);
                            rvSwipeTarget.setAdapter(mAdapter);

                            mAdapter.setOnItemClickListener(new SearchItemAdapter.OnRecyclerViewItemClickListener() {
                                @Override
                                public void onItemClick(View view) {
                                    int i = rvSwipeTarget.getChildAdapterPosition(view);
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.VIEW");
                                    Uri content_url = Uri.parse(searchBeenList.get(i).getLinkHref());
                                    intent.setData(content_url);
                                    startActivity(intent);
                                }

                                @Override
                                public void onItemLongClick(View view) {

                                }
                            });
                        }
                    });
                } else {
                    return;
                }
            }
        });
    }

    public void gsonParseJson(String data) {
        Gson gson = new Gson();
        JSONObject jsonObject;
        String jsonData = null;
        try {
            jsonObject = new JSONObject(data);
            jsonData = jsonObject.getString("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (searchBeenList == null || searchBeenList.size() == 0) {
            searchBeenList = gson.fromJson(jsonData, new TypeToken<List<SearchBean>>() {
            }.getType());
        } else {
            List<SearchBean> more = gson.fromJson(jsonData, new TypeToken<List<SearchBean>>() {
            }.getType());
            searchBeenList.addAll(more);
        }
    }

    public void hotRequestData(String URL) {
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
                        String jsonFileData = openLocalFileUtils();
                        if (jsonFileData == "none" || jsonFileData.length() == 4) {
                            Toast.makeText(getActivity(), "没有文件", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            gsonJson2(jsonFileData);
                        }

                        labelAdapter.setList(getActivity(), labelBeenList);
                        rvHotSwipeTarget.setAdapter(labelAdapter);

                        labelAdapter.setmOnItemClickListener(new LabelAdapter.OnRVItemClickListener() {
                            @Override
                            public void onItemClick(View view) {
                                int i = rvHotSwipeTarget.getChildAdapterPosition(view);
                                Intent intent = new Intent(getActivity(), LabelInfoActivity.class);
                                intent.putExtra("keyLabel", labelBeenList.get(i).getLabelTitle());
                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongClick(View view) {

                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (json.equals("none") || json.length() == 4 || json == null) {
                                return;
                            }

                            gsonJson2(json);

                            saveLocalFileUtils(getActivity(), json);

                            labelAdapter.setList(getActivity(), labelBeenList);
                            rvHotSwipeTarget.setAdapter(labelAdapter);

                            labelAdapter.setmOnItemClickListener(new LabelAdapter.OnRVItemClickListener() {
                                @Override
                                public void onItemClick(View view) {
                                    int i = rvHotSwipeTarget.getChildAdapterPosition(view);

                                    Intent intent = new Intent(getActivity(), LabelInfoActivity.class);
                                    intent.putExtra("keyLabel", labelBeenList.get(i).getLabelTitle());
                                    startActivity(intent);
                                }

                                @Override
                                public void onItemLongClick(View view) {
                                    int i = rvHotSwipeTarget.getChildAdapterPosition(view);
                                    myListener.sendContent(labelBeenList.get(i).getLabelTitle());//将内容进行回传
                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.layFrame, SearchAfterFragment.newInstance(labelBeenList.get(i).getLabelTitle()));
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                                }
                            });
                        }
                    });
                } else {
                    return;
                }
            }
        });
    }

    public void saveLocalFileUtils(Context context, String json) {
        String fileListName[] = context.fileList();
        String fileName = "hotSearch.json";
        try {
            for (int i = 0; i < fileListName.length; i++) {
                if (fileListName[i].equals(fileName)) {
                    context.deleteFile(fileName);
                }
                FileOutputStream os = getActivity().openFileOutput("hotSearch.json", Context.MODE_PRIVATE);
                //写数据
                os.write(json.getBytes());
                os.close();//关闭文件
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String openLocalFileUtils() {
        String fileListName[] = getActivity().fileList();
        String fileName = "hotSearch.json";
        byte buffer[] = new byte[4096];
        int len = 0;
        String data = null;
        try {
            for (int i = 0; i < fileListName.length; i++) {
                if (fileListName[i].equals(fileName)) {
                    FileInputStream in = getActivity().openFileInput("hotSearch.json");
                    //将数据读到buffer.
                    len = in.read(buffer);
                    in.close();//关闭文件

                    data = new String(buffer, 0, len);
                } else {
                    data = "none";
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void gsonJson2(String json) {
        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject(json);
            String jsonData = jsonObject.getString("data");
            if (labelBeenList == null || labelBeenList.size() == 0) {
                labelBeenList = gson.fromJson(jsonData, new TypeToken<List<LabelBean>>() {
                }.getType());
            } else {
                List<LabelBean> more = gson.fromJson(jsonData, new TypeToken<List<LabelBean>>() {
                }.getType());
                labelBeenList.addAll(more);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myListener = (MyListener) getActivity();
    }

//    private void initData() {
//        String[] array = orStr.split(";");
//        for (int i = 0; i < array.length; i++) {
//            titleList.add(array[i]);
//        }
//        addView();
//    }
//
//    private void addView() {
//        for (int i = 0; i < titleList.size(); i++) {
//            View view = View.inflate(getActivity(), R.layout.item_label_1, null);
//            TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
//            tvTitle.setText(titleList.get(i));
//            final int finalI = i;
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    myListener.sendContent(titleList.get(finalI));
//                    Toast.makeText(getActivity(), "点击了" + titleList.get(finalI), Toast.LENGTH_SHORT).show();
//                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                    transaction.replace(R.id.layFrame, SearchAfterFragment.newInstance(titleList.get(finalI)));
//                    transaction.addToBackStack(null);
//                    transaction.commit();
//                }
//            });
//            mLinearAddView.addView(view);
//        }
//    }

    private int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}
