package com.fxd.wangluo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fxd.wangluo.Bean.UserBean;
import com.fxd.wangluo.SQLite.DataBaseHandler;
import com.fxd.wangluo.utils.IconManager;

import java.util.List;

public class IncludeFragment extends Fragment {
    private IconManager iconManager;
    private String iconPath = "fonts/ionicons.ttf";
    private DataBaseHandler handlerDB;
    private UserBean userInfoDB;
    private List<UserBean> checkUserList;
    private TextView tvBull;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_include, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handlerDB = new DataBaseHandler(getActivity());
        iconManager = new IconManager();
        ((TextView) view.findViewById(R.id.icon_throw_tv))
                .setTypeface(iconManager.getIcons(iconPath, getContext()));

        initView(view);

    }

    public void initView(View v) {
        tvBull = (TextView) v.findViewById(R.id.tv_bull);
        String largeText = getResources().getString(R.string.large_text);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvBull.setText(Html.fromHtml(largeText, Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvBull.setText(Html.fromHtml(largeText));
        }
        Button btnIntentInclude = (Button) v.findViewById(R.id.btn_intent_include);
        btnIntentInclude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), IncludeActivity.class));
            }
        });
    }
}
