package com.example.a10767.electronic_wardrobe.Fix_Message;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
 * 个人信息里面修改绑定的手机号
 */
public class FixPhone extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout phone_return; //手机号码返回
    private Button phone_get_validate; //手机验证码获取
    private Button phone_save;  //手机号码保存
    private EditText myself_phone_Ed;//手机号
    private EditText myself_validate_Ed;//验证码
    private String change_phone_Ed; //字符串接收(电话号码)
    private String responseData_Vaildate; //验证码接受

    private static final String TAG = "FixPhone";
    private final int FORGETPASSOWRD_SUCCESS = 1;
    private final int NET_ERROR = -1;
    private final int FAIL = 2;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FORGETPASSOWRD_SUCCESS:
                    Toast.makeText(FixPhone.this, "修改成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FixPhone.this, MyselfMine.class));
                    overridePendingTransition(R.anim.enter2, R.anim.quit2);
                    break;
                case NET_ERROR:
                    Toast.makeText(FixPhone.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                    break;
                case FAIL:
                    Toast.makeText(FixPhone.this, "该手机已被注册", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myself_fix_phone);
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
        phone_return.setOnClickListener(this);
        phone_get_validate.setOnClickListener(this);
        phone_save.setOnClickListener(this);
    }

    private void initUI() {
        phone_return = findViewById(R.id.phone_return);
        phone_get_validate = findViewById(R.id.phone_get_validate);
        phone_save = findViewById(R.id.phone_save);
        myself_phone_Ed = findViewById(R.id.myself_phone_Ed);
        myself_validate_Ed = findViewById(R.id.myself_validate_Ed);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.phone_return:
                startActivity(new Intent(this, MyselfMine.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.phone_get_validate:
                initGetVaildateCode();//获取验证码
                break;
            case R.id.phone_save:
                checkForgetPassword();//判断
                break;
        }

    }

    /**
     * 获取验证码
     */
    private void initGetVaildateCode() {
        change_phone_Ed = myself_phone_Ed.getText().toString();
        if (!TextUtils.isEmpty(change_phone_Ed)) {
            OkHttpUtil.okhttputil_validate(url + "LoginTest2/personbyphone1.action", change_phone_Ed, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(NET_ERROR);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    responseData_Vaildate = response.body().string();//传回验证码
                    if (responseData_Vaildate.equals("fail")) {
                        handler.sendEmptyMessage(FAIL);
                    }
                }
            });
        } else {
            Toast.makeText(FixPhone.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 检查信息
     */
    private void checkForgetPassword() {
        String myself_validate_Ed2 = myself_validate_Ed.getText().toString();
        if (TextUtils.isEmpty(change_phone_Ed)) {
            Toast.makeText(FixPhone.this, "修改未成功", Toast.LENGTH_SHORT).show();
        } else {
            if (!myself_validate_Ed2.equals(responseData_Vaildate)) {
                Toast.makeText(FixPhone.this, "验证码不正确请重新输入", Toast.LENGTH_SHORT).show();
            } else {
                sendNewPhoneNumber();//上传新电话号码
            }
        }
    }

    /**
     * 上传新电话号码
     */
    private void sendNewPhoneNumber() {
        OkHttpUtil.okhttputil_fix_phoneNumber(url + "LoginTest2/personbyphone2.action", login_account_Ed, change_phone_Ed, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(NET_ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData_Login = response.body().string();//传回注册数据
                if (responseData_Login.equals("success")) {
                    handler.sendEmptyMessage(FORGETPASSOWRD_SUCCESS);
                } else
                    handler.sendEmptyMessage(FAIL);
            }
        });
    }
}
