package com.example.a10767.electronic_wardrobe.Main_Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.Others_Wardrobe.OthersWardrobe;
import com.example.a10767.electronic_wardrobe.R;

import static com.example.a10767.electronic_wardrobe.StaticVariable.themain;
import static com.example.a10767.electronic_wardrobe.StaticVariable.isExit;


/**
 * Created by 10767 on 2018/6/28.
 */

/**
 * 下面连接整个碎片的标题栏
 */

public class TheMain extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TheMain";
    LinearLayout myself_wardrobe_button;
    LinearLayout myself_message_button;
    LinearLayout myself_mix_button;
    LinearLayout others_wardrobe;

    Handler mHandler2 = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.the_main);
        Fragmentcheck();//判断返回后展示哪个碎片
        initUI();
        initView();
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void Fragmentcheck() {
        switch (themain) {
            case 1:
                replaceFragment(new MyselfWardrobeFragment());
                break;
            case 2:
                replaceFragment(new MyselfMessageFragment());
                break;
            case 3:
                replaceFragment(new MixFragment());
                break;

        }
    }

    private void initView() {
        myself_wardrobe_button.setOnClickListener(this);
        myself_message_button.setOnClickListener(this);
        myself_mix_button.setOnClickListener(this);
        others_wardrobe.setOnClickListener(this);

    }


    private void initUI() {
        myself_message_button = findViewById(R.id.myself_message_button);
        myself_wardrobe_button = findViewById(R.id.myself_wardrobe_button);
        myself_mix_button = findViewById(R.id.myself_mix_button);
        others_wardrobe = findViewById(R.id.others_wardrobe);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.myself_wardrobe_button:
                themain = 1;
                replaceFragment(new MyselfWardrobeFragment());
                break;
            case R.id.myself_message_button:
                themain = 2;
                replaceFragment(new MyselfMessageFragment());
                break;
            case R.id.myself_mix_button:
                themain = 3;
                replaceFragment(new MixFragment());
                break;
            case R.id.others_wardrobe:
                startActivity(new Intent(TheMain.this, OthersWardrobe.class));
                break;

        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.FrameLayout, fragment);
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {  //该方法用来捕捉手机键盘被按下的事件。
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler2.sendEmptyMessageDelayed(0, 2000); //过了两秒走这个方法
        } else {
            ActivityCollector.finaishAll();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }
}
