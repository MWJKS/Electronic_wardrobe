package com.example.a10767.electronic_wardrobe.Fix_Message;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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
 * Created by 10767 on 2018/7/13.
 */

/**
 * 个人信息里面的修改密码
 */
public class FixPassword extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout password_return; //修改密码返回
    private Button password_save; //密码保存
    private EditText myself_password_Ed1; //修改密码第一次
    private EditText myself_password_Ed2; //确认密码
    private String change_password_Ed; //字符串接受
    private String change_password_again_Ed; //字符串接受2
    private static final String TAG = "FixPassword";
    private final int CHANGEPASSWORDSUCCESS = 1;
    private final int NET_ERROR = -1;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHANGEPASSWORDSUCCESS:
                    Toast.makeText(FixPassword.this, "修改成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FixPassword.this, MyselfMine.class));
                    overridePendingTransition(R.anim.enter2, R.anim.quit2);
                    break;
                case NET_ERROR:
                    Toast.makeText(FixPassword.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myself_fix_password);
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
        password_return.setOnClickListener(this);
        password_save.setOnClickListener(this);
    }

    private void initUI() {
        password_return = findViewById(R.id.password_return);
        password_save = findViewById(R.id.password_save);
        myself_password_Ed1 = findViewById(R.id.myself_password_Ed1);
        myself_password_Ed2 = findViewById(R.id.myself_password_Ed2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.password_return:
                startActivity(new Intent(this, MyselfMine.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.password_save:
                submit_finish();
                break;
        }

    }

    /**
     * 提交完成
     */
    private void submit_finish() {
        change_password_Ed = myself_password_Ed1.getText().toString();
        change_password_again_Ed = myself_password_Ed2.getText().toString();
        if ((TextUtils.isEmpty(change_password_Ed) && TextUtils.isEmpty(change_password_again_Ed))) {
            Toast.makeText(FixPassword.this, "密码不能为空", Toast.LENGTH_SHORT).show();
        } else {
            if (!change_password_Ed.equals(change_password_again_Ed)) {
                Toast.makeText(FixPassword.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            } else {
                OkHttpUtil.okhttputil_fix_Password(url + "LoginTest2/personbyother.action", login_account_Ed, change_password_Ed, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        handler.sendEmptyMessage(NET_ERROR);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, login_account_Ed + change_password_Ed);
                        String responseData_Login = response.body().string();//传回注册数据
                        if (responseData_Login.equals("success")) {
                            handler.sendEmptyMessage(CHANGEPASSWORDSUCCESS);
                        } else
                            handler.sendEmptyMessage(NET_ERROR);
                    }
                });
            }
        }
    }
}
