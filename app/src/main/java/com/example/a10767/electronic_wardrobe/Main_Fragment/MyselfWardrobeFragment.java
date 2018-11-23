package com.example.a10767.electronic_wardrobe.Main_Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Accessories;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Bag;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Cap;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Coat;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Jacket;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Pants;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Shoe;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Skirt;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Suit;
import com.example.a10767.electronic_wardrobe.R;

/**
 * Created by 10767 on 2018/7/2.
 */

/**
 * 我的衣柜碎片
 */

public class MyselfWardrobeFragment extends Fragment implements View.OnClickListener {
    private LinearLayout jacket;
    private LinearLayout skirt;
    private LinearLayout pants;
    private LinearLayout shoe;
    private LinearLayout suit;
    private LinearLayout bag;
    private LinearLayout coat;
    private LinearLayout cap;
    private LinearLayout accessories;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myself_wardrobe, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
        initView();
    }

    private void initUI() {
        jacket = getActivity().findViewById(R.id.jacket);  //上衣布局
        skirt = getActivity().findViewById(R.id.skirt); //裙子布局
        pants=getActivity().findViewById(R.id.pants); //裤子布局
        shoe=getActivity().findViewById(R.id.shoe); //鞋子布局
        suit=getActivity().findViewById(R.id.suit); //套装布局
        bag=getActivity().findViewById(R.id.bag);  //包布局
        coat=getActivity().findViewById(R.id.coat); //外套布局
        cap=getActivity().findViewById(R.id.cap); //帽子布局
        accessories=getActivity().findViewById(R.id.accessories); //首饰布局
    }

    private void initView() {
        jacket.setOnClickListener(this);
        skirt.setOnClickListener(this);
        pants.setOnClickListener(this);
        shoe.setOnClickListener(this);
        suit.setOnClickListener(this);
        bag.setOnClickListener(this);
        coat.setOnClickListener(this);
        cap.setOnClickListener(this);
        accessories.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.jacket:
                startActivity(new Intent(getContext(), Clothe_Jacket.class));
                break;
            case R.id.skirt:
                startActivity(new Intent(getContext(), Clothe_Skirt.class));
                break;
            case R.id.pants:
                startActivity(new Intent(getContext(), Clothe_Pants.class));
                break;
            case R.id.shoe:
                startActivity(new Intent(getContext(), Clothe_Shoe.class));
                break;
            case R.id.suit:
                startActivity(new Intent(getContext(), Clothe_Suit.class));
                break;
            case R.id.bag:
                startActivity(new Intent(getContext(), Clothe_Bag.class));
                break;
            case R.id.coat:
                startActivity(new Intent(getContext(), Clothe_Coat.class));
                break;
            case R.id.cap:
                startActivity(new Intent(getContext(), Clothe_Cap.class));
                break;
            case R.id.accessories:
                startActivity(new Intent(getContext(), Clothe_Accessories.class));
                break;
        }
    }
}
