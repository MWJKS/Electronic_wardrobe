package com.example.a10767.electronic_wardrobe.Fix_Message;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.OkHttpUtil;
import com.example.a10767.electronic_wardrobe.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.a10767.electronic_wardrobe.StaticVariable.url;


/**
 * Created by 10767 on 2018/6/24.
 */

/**
 * 忘记密码
 */
public class ForgetPassword extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    String change_validate_code_Ed;
    String responseData_Vaildate;
    String change_phone_Ed;
    EditText change_phone;
    EditText change_validate_code;
    Button change_validate_code_button;
    Button change_next_button;

    public static String phone = "";
    private final int FORGETPASSOWRD_SUCCESS = 1;
    private final int NET_ERROR = -1;
    private final int FAIL = 2;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FORGETPASSOWRD_SUCCESS:
                    Toast.makeText(ForgetPassword.this, "注册成功", Toast.LENGTH_SHORT).show();
                    break;
                case NET_ERROR:
                    Toast.makeText(ForgetPassword.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                    break;
                case FAIL:
                    Toast.makeText(ForgetPassword.this, "手机号码输入有误，请重新输入", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);
        initUI();
        initView();
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initUI() {
        change_phone = findViewById(R.id.change_phone);//修改电话号码时的绑定
        change_validate_code = findViewById(R.id.change_validate_code);//修改的验证码
        change_validate_code_button = findViewById(R.id.change_validate_code_button);//修改验证码的按钮
        change_next_button = findViewById(R.id.change_next_button);//忘记密码的下一步
        pref = getSharedPreferences("ForgetPassword", MODE_PRIVATE);
        editor = pref.edit();
    }

    private void initView() {
        change_validate_code_button.setOnClickListener(this);
        change_next_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_validate_code_button://获取验证码按钮
                initGetVaildateCode();//获取验证码
                break;
            case R.id.change_next_button://下一步
                initEditText();
                break;
        }

    }

    /**
     * 获取验证码
     */
    private void initGetVaildateCode() {
        String change_phone_Ed = change_phone.getText().toString();
        if (!TextUtils.isEmpty(change_phone_Ed)) {
            OkHttpUtil.okhttputil_validate(url + "LoginTest2/updateinfor.action", change_phone_Ed, new Callback() {
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
            Toast.makeText(ForgetPassword.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 编辑框内容获取
     */
    private void initEditText() {
        change_phone_Ed = change_phone.getText().toString();
        change_validate_code_Ed = change_validate_code.getText().toString();
        checkForgetPassword();//检查信息
    }

    /**
     * 检查信息
     */
    private void checkForgetPassword() {

        if (TextUtils.isEmpty(change_phone_Ed)) {
            Toast.makeText(ForgetPassword.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
        } else {
            if (!change_validate_code_Ed.equals(responseData_Vaildate)) {
                Toast.makeText(ForgetPassword.this, "验证码不正确请重新输入", Toast.LENGTH_SHORT).show();
            } else {
                phone = change_phone_Ed;
                Intent ChangePassword_intent = new Intent(ForgetPassword.this, ChangePassword.class);
                startActivity(ChangePassword_intent);
            }
        }
    }
}
