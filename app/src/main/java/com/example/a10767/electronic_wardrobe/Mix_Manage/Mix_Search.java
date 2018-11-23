package com.example.a10767.electronic_wardrobe.Mix_Manage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.Clothes_Manage.SearchFragment;
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

import static com.example.a10767.electronic_wardrobe.StaticVariable.clo_name;
import static com.example.a10767.electronic_wardrobe.StaticVariable.search_content;

/**
 * Created by 10767 on 2018/7/31.
 */

public class Mix_Search extends AppCompatActivity implements View.OnClickListener {
    private Button clothe_search_IMG_Bt;
    private EditText clothe_search_Ed;
    private LinearLayout clothe_search_return;


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clothe_search);
        initUI();
        initView();
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
    private void initView() {
        clothe_search_IMG_Bt.setOnClickListener(this);
        clothe_search_return.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Mix_Search.this, My_Mix.class));
        overridePendingTransition(R.anim.enter2, R.anim.quit2);
    }

    private void initUI() {
        clothe_search_return = findViewById(R.id.clothe_search_return);
        clothe_search_IMG_Bt = findViewById(R.id.clothe_search_IMG_Bt);
        clothe_search_Ed = findViewById(R.id.clothe_search_Ed);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clothe_search_IMG_Bt:
                search_content = clothe_search_Ed.getText().toString();
                if (!TextUtils.isEmpty(search_content)) {
                    replaceFragment(new MixSearchFragment());
                }
                if (TextUtils.isEmpty(search_content)) {
//                    replaceFragment(new SearchFragment()); //没有找到信息
                }
                break;
            case R.id.clothe_search_return:
                startActivity(new Intent(Mix_Search.this, My_Mix.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.FrameLayout_search, fragment);
        transaction.commit();
    }
}
