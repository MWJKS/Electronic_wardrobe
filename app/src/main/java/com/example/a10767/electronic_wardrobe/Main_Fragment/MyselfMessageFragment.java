package com.example.a10767.electronic_wardrobe.Main_Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.a10767.electronic_wardrobe.R;

/**
 * Created by 10767 on 2018/7/2.
 */


/**
 * 我的信息碎片
 */

public class MyselfMessageFragment extends Fragment implements View.OnClickListener {
    private LinearLayout myself_mine_L;  //个人信息
    private LinearLayout myself_collect_L; //我的收藏

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myself_message, container, false);
        return view;
}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
        initView();
    }

    private void initView() {
        myself_mine_L.setOnClickListener(this);
        myself_collect_L.setOnClickListener(this);
    }

    private void initUI() {
        myself_mine_L = getActivity().findViewById(R.id.myself_mine_L);
        myself_collect_L=getActivity().findViewById(R.id.myself_collect_L);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.myself_mine_L:
                startActivity(new Intent(getActivity(), MyselfMine.class));
                getActivity().overridePendingTransition(R.anim.enter,R.anim.quit);
                break;
            case R.id.myself_collect_L:
                startActivity(new Intent(getActivity(), MyselfCollect.class));
                getActivity().overridePendingTransition(R.anim.enter,R.anim.quit);
                break;
        }

    }

}
