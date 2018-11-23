package com.example.a10767.electronic_wardrobe.Main_Fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a10767.electronic_wardrobe.Mix_Manage.Clothe_Mix;
import com.example.a10767.electronic_wardrobe.Mix_Manage.My_Mix;
import com.example.a10767.electronic_wardrobe.R;

import static com.example.a10767.electronic_wardrobe.StaticVariable.mix_choice;

/**
 * Created by 10767 on 2018/8/3.
 */

public class MixFragment extends Fragment implements View.OnClickListener {
    private TextView mix_main_button;
    private TextView mix_main_mine_button;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mix_main, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
        initView();
    }

    private void initView() {
        mix_main_button.setOnClickListener(this);
        mix_main_mine_button.setOnClickListener(this);
    }

    private void initUI() {
        mix_main_button = getActivity().findViewById(R.id.mix__main_button);
        mix_main_mine_button = getActivity().findViewById(R.id.mix_main_mine_button);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mix__main_button:
                mix_choice="Mix";
                startActivity(new Intent(getActivity(), Clothe_Mix.class));
                break;
            case R.id.mix_main_mine_button:
                startActivity(new Intent(getActivity(), My_Mix.class));
                break;
        }

    }
}
