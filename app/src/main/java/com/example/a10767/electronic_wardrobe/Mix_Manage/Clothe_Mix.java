package com.example.a10767.electronic_wardrobe.Mix_Manage;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import static com.example.a10767.electronic_wardrobe.StaticVariable.frame_number;
import static com.example.a10767.electronic_wardrobe.StaticVariable.mix_choice;
import static com.example.a10767.electronic_wardrobe.StaticVariable.themain;
import static com.example.a10767.electronic_wardrobe.StaticVariable.mixFrameList;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.Main_Fragment.TheMain;
import com.example.a10767.electronic_wardrobe.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10767 on 2018/8/3.
 */

/**
 * 框架选择活动
 */
public class Clothe_Mix extends AppCompatActivity implements View.OnClickListener {
    MixAdapter mixAdapter;
    private List<Frame> frameList = new ArrayList<>();
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    private Mix mix;
    private LinearLayout clothe_mix_next;
    private LinearLayout clothe_mix_return;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clothe_mix);
        initUI();
        initView();
        initClothes();
        ActivityCollector.addActivity(this);
        recyclerView.setAdapter(mixAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mix_choice.equals("myMix"))
        {
            startActivity(new Intent(Clothe_Mix.this, My_Mix.class));
            overridePendingTransition(R.anim.enter2, R.anim.quit2);
        }
        if (mix_choice.equals("Mix")){
            themain = 3;
            startActivity(new Intent(Clothe_Mix.this, TheMain.class));
            overridePendingTransition(R.anim.enter2, R.anim.quit2);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initClothes() {
        Frame frame1 = new Frame(1, BitmapFactory.decodeResource(getResources(), R.drawable.kuangjia_1));
        frameList.add(frame1);
        Frame frame2 = new Frame(2, BitmapFactory.decodeResource(getResources(), R.drawable.kuangjia_2));
        frameList.add(frame2);
        Frame frame3 = new Frame(3, BitmapFactory.decodeResource(getResources(), R.drawable.kuangjia_3));
        frameList.add(frame3);
    }

    private void initView() {
        recyclerView.setLayoutManager(linearLayoutManager);
        clothe_mix_next.setOnClickListener(this);
        clothe_mix_return.setOnClickListener(this);
    }

    private void initUI() {
        frame_number = 0;
        mixFrameList = new ArrayList<>();//在这里初始化选择衣物的集合集合
        mixAdapter = new MixAdapter(frameList);
        recyclerView = findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        clothe_mix_next = findViewById(R.id.clothe_mix_next);
        clothe_mix_return = findViewById(R.id.clothe_mix_return);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clothe_mix_return:
                if(mix_choice.equals("myMix"))
                {
                    startActivity(new Intent(Clothe_Mix.this, My_Mix.class));
                    overridePendingTransition(R.anim.enter2, R.anim.quit2);
                }
                if (mix_choice.equals("Mix")){
                    themain = 3;
                    startActivity(new Intent(Clothe_Mix.this, TheMain.class));
                    overridePendingTransition(R.anim.enter2, R.anim.quit2);
                }
                break;
            case R.id.clothe_mix_next:
                if (frame_number == 0) {
                    Toast.makeText(Clothe_Mix.this, "请选择一个模板", Toast.LENGTH_SHORT).show();
                }
                if (frame_number == 1) {
                    uploadClothes1();
                    startActivity(new Intent(Clothe_Mix.this, Frame_All.class));
                    overridePendingTransition(R.anim.enter, R.anim.quit);
                }
                if (frame_number == 2) {
                    uploadClothes2();
                    startActivity(new Intent(Clothe_Mix.this, Frame_All.class));
                    overridePendingTransition(R.anim.enter, R.anim.quit);
                }
                if (frame_number == 3) {
                    uploadClothes4();
                    startActivity(new Intent(Clothe_Mix.this, Frame_All.class));
                    overridePendingTransition(R.anim.enter, R.anim.quit);
                }
                break;
        }
    }

    /*加载衣物信息*/
    private void uploadClothes1() {
        for (int i = 0; i < 4; i++) {
            mix = new Mix(BitmapFactory.decodeResource(getResources(), R.drawable.mix_add));
            mixFrameList.add(mix);
        }
    }
    /*加载衣物信息*/
    private void uploadClothes2() {
        for (int i = 0; i < 3; i++) {
            mix = new Mix(BitmapFactory.decodeResource(getResources(), R.drawable.mix_add));
            mixFrameList.add(mix);
        }
    }
    /*加载衣物信息*/
    private void uploadClothes4() {
        for (int i = 0; i < 2; i++) {
            mix = new Mix(BitmapFactory.decodeResource(getResources(), R.drawable.mix_add));
            mixFrameList.add(mix);
        }
    }
}
