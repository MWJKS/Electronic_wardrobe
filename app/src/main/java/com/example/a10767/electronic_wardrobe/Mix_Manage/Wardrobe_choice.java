package com.example.a10767.electronic_wardrobe.Mix_Manage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.R;


/**
 * Created by 10767 on 2018/8/7.
 */

/**
 * 衣柜选择(暂是只有上衣模块可供选择)
 */
public class Wardrobe_choice extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout wardrobe_choice_return;
    private TextView jacket_wardrobe_bt;
    private TextView skirt_wardrobe_bt;
    private TextView pants_wardrobe_bt;
    private TextView shoe_wardrobe_bt;
    private TextView suit_wardrobe_bt;
    private TextView coat_wardrobe_bt;
    private TextView cap_wardrobe_bt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wardrobe_choice);
        initUI();
        initView();
        ActivityCollector.addActivity(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Wardrobe_choice.this, Frame_All.class));
        overridePendingTransition(R.anim.enter2, R.anim.quit2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initView() {
        wardrobe_choice_return.setOnClickListener(this);
        jacket_wardrobe_bt.setOnClickListener(this);
        skirt_wardrobe_bt.setOnClickListener(this);
        pants_wardrobe_bt.setOnClickListener(this);
        shoe_wardrobe_bt.setOnClickListener(this);
        suit_wardrobe_bt.setOnClickListener(this);
        coat_wardrobe_bt.setOnClickListener(this);
        cap_wardrobe_bt.setOnClickListener(this);
    }

    private void initUI() {
        wardrobe_choice_return = findViewById(R.id.wardrobe_choice_return);
        jacket_wardrobe_bt = findViewById(R.id.jacket_wardrobe_bt);
        skirt_wardrobe_bt = findViewById(R.id.skirt_wardrobe_bt);
        pants_wardrobe_bt = findViewById(R.id.pants_wardrobe_bt);
        shoe_wardrobe_bt = findViewById(R.id.shoe_wardrobe_bt);
        suit_wardrobe_bt = findViewById(R.id.suit_wardrobe_bt);
        coat_wardrobe_bt = findViewById(R.id.coat_wardrobe_bt);
        cap_wardrobe_bt = findViewById(R.id.cap_wardrobe_bt);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wardrobe_choice_return:
                startActivity(new Intent(Wardrobe_choice.this, Frame_All.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.jacket_wardrobe_bt:
                startActivity(new Intent(Wardrobe_choice.this, Mix_Jacket.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.skirt_wardrobe_bt:
                startActivity(new Intent(Wardrobe_choice.this, Mix_Skirt.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.pants_wardrobe_bt:
                startActivity(new Intent(Wardrobe_choice.this, Mix_Pants.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.shoe_wardrobe_bt:
                startActivity(new Intent(Wardrobe_choice.this, Mix_Shoe.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.suit_wardrobe_bt:
                startActivity(new Intent(Wardrobe_choice.this, Mix_Suit.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.coat_wardrobe_bt:
                startActivity(new Intent(Wardrobe_choice.this, Mix_Coat.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.cap_wardrobe_bt:
                startActivity(new Intent(Wardrobe_choice.this, Mix_Cap.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
        }
    }
}
