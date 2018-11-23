package com.example.a10767.electronic_wardrobe.Fix_Message;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.Main_Fragment.MyselfMine;
import com.example.a10767.electronic_wardrobe.OkHttpUtil;
import com.example.a10767.electronic_wardrobe.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.a10767.electronic_wardrobe.StaticVariable.login_account_Ed;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;


/**
 * 修改性别
 * Created by 10767 on 2018/7/12.
 */

public class MyselfGender extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout gender_return;
    private LinearLayout man_button; //男选项
    private LinearLayout women_button; //女选项
    private Button gender_save;//性别保存按钮
    private ImageView man_check; //男对勾
    private ImageView women_check; // 女对勾
    private String gender = "未绑定"; //性别字符串
    private static final String TAG = "MyselfGender";

    private final int FAX_SUCCESS = 1;
    private final int NET_ERROR = -1;
    private final int FAX_FAIL = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FAX_SUCCESS:
                    Toast.makeText(MyselfGender.this, "修改成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MyselfGender.this, MyselfMine.class));
                    overridePendingTransition(R.anim.enter2, R.anim.quit2);
                    break;
                case NET_ERROR:
                    Toast.makeText(MyselfGender.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                    break;
                case FAX_FAIL:
                    Toast.makeText(MyselfGender.this, "修改失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myself_gender);
        initUI();
        check();
        initView();
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * 判断显示对勾情况
     */
    private void check() {
        man_check.setVisibility(View.GONE);
        women_check.setVisibility(View.GONE);

    }

    private void initView() {
        gender_return.setOnClickListener(this);
        women_button.setOnClickListener(this);
        man_button.setOnClickListener(this);
        gender_save.setOnClickListener(this);
    }

    private void initUI() {
        gender_return = findViewById(R.id.gender_return);
        man_button = findViewById(R.id.man_button);
        women_button = findViewById(R.id.women_button);
        gender_save = findViewById(R.id.gender_save);
        man_check = findViewById(R.id.man_check);
        women_check = findViewById(R.id.women_check);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gender_return:
                startActivity(new Intent(this, MyselfMine.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.man_button:
                gender = "男";
                Log.d(TAG, "男");
                women_check.setVisibility(View.GONE);
                man_check.setVisibility(View.VISIBLE);
                break;
            case R.id.women_button:
                gender = "女";
                Log.d(TAG, "女");
                man_check.setVisibility(View.GONE);
                women_check.setVisibility(View.VISIBLE);
                break;
            case R.id.gender_save:
                uploadGender();
                break;
        }

    }

    private void uploadGender() {
        OkHttpUtil.send_gender(url + "LoginTest2/personbyother.action", login_account_Ed, gender, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(NET_ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData_Gender = response.body().string();
                Log.d(TAG, responseData_Gender);
                if (responseData_Gender.equals("success")) {
                    handler.sendEmptyMessage(FAX_SUCCESS);
                } else
                    handler.sendEmptyMessage(FAX_FAIL);
            }
        });
    }
}
