package com.example.a10767.electronic_wardrobe.Fix_Message;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
 * Created by 10767 on 2018/7/12.
 *
 * 修改用户名
 */

public class MyselfUsername extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MyselfUsername";
    private LinearLayout username_return;//返回按钮
    private Button username_save;//用户名保存
    private EditText myself_username_Ed;//用户名编辑框
    private String useralias = "未绑定";//用户名字符串

    private final int FAX_SUCCESS = 1;
    private final int NET_ERROR = -1;
    private final int FAX_FAIL = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FAX_SUCCESS:
                    Toast.makeText(MyselfUsername.this, "修改成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MyselfUsername.this, MyselfMine.class));
                    overridePendingTransition(R.anim.enter2, R.anim.quit2);
                    break;
                case NET_ERROR:
                    Toast.makeText(MyselfUsername.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                    break;
                case FAX_FAIL:
                    Toast.makeText(MyselfUsername.this, "错误格式，请重新修改", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myself_username);
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
        username_return.setOnClickListener(this);
        username_save.setOnClickListener(this);
    }

    private void initUI() {
        username_return = findViewById(R.id.username_return);
        username_save = findViewById(R.id.username_save);
        myself_username_Ed = findViewById(R.id.myself_username_Ed);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.username_return: //用户名
                startActivity(new Intent(this, MyselfMine.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.username_save:  //用户名保存并上传
                saveUsername();//先保存
                break;

        }
    }

    /**
     * 保存用户名
     */
    private void saveUsername() {
        useralias = myself_username_Ed.getText().toString();//获取用户名
        uploadUsername();
    }

    /**
     * 上传用户名
     */
    private void uploadUsername() {
        OkHttpUtil.send_username(url + "LoginTest2/personbyother.action", login_account_Ed, useralias, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(NET_ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData_Login = response.body().string();
                if (responseData_Login.equals("success")) {
                    handler.sendEmptyMessage(FAX_SUCCESS);
                } else
                    handler.sendEmptyMessage(FAX_FAIL);
            }
        });

    }
}
