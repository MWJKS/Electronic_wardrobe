package com.example.a10767.electronic_wardrobe.Login_Register;

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
 * Created by 10767 on 2018/6/19.
 */

/**
 * 注册账号
 */
public class Register extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Register";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    EditText register_account;
    EditText register_password;
    EditText register_password_again;
    EditText register_find_phoneNumber;
    EditText register_validate_code;
    Button register_validate_code_button;
    Button register_register_button;


    private final int REGIRSTER_SUCCESS = 1;
    private final int NET_ERROR = -1;
    private final int REGIRSTER_FAIL = 2;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REGIRSTER_SUCCESS:
                    Toast.makeText(Register.this, "注册成功", Toast.LENGTH_SHORT).show();
                    break;
                case NET_ERROR:
                    Toast.makeText(Register.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                    break;
                case REGIRSTER_FAIL:
                    Toast.makeText(Register.this, "该账号已被注册", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        initUI();//初始化UI
        initView();//初始化监听
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        register_account = findViewById(R.id.register_account);//手机号填写处
        register_password = findViewById(R.id.register_password);//密码填写处
        register_password_again = findViewById(R.id.register_password_again);//二次密码填写处
        register_find_phoneNumber = findViewById(R.id.find_phone_number);//找回账号的手机密码
        register_validate_code = findViewById(R.id.register_validate_code);//验证码填写处
        register_validate_code_button = findViewById(R.id.register_validate_code_button);//注册中获取验证码按钮
        register_register_button = findViewById(R.id.register_register_button);//注册中的注册按钮

        pref = getSharedPreferences("Register", MODE_PRIVATE);
        editor = pref.edit();

    }

    /**
     * 初始化监听
     */
    private void initView() {
        register_register_button.setOnClickListener(this);//注册中的注册按钮点击处
        register_validate_code_button.setOnClickListener(this);//获取验证码点击处
    }

    /**
     * 获取编辑框中的内容
     */
    private void initEditText() {
        String register_account_Ed = register_account.getText().toString();
        String register_password_Ed = register_password.getText().toString();
        String register_password_again_Ed = register_password_again.getText().toString();
        String register_find_phoneNumber_Ed = register_find_phoneNumber.getText().toString();
        String register_validate_code_Ed = register_validate_code.getText().toString();

        /* 储存信息*/

        editor.putString("register_account_Ed", register_account_Ed);
        editor.putString("register_password_Ed", register_password_Ed);
        editor.putString("register_password_again_Ed", register_password_again_Ed);
        editor.putString("register_find_phoneNumber_Ed", register_find_phoneNumber_Ed);
        editor.putString("register_validate_code_Ed", register_validate_code_Ed);
        editor.apply();

        checkRegisterMessage();//注册框中内容判断
    }


    /**
     * 点击方法
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_register_button:
                initEditText(); //编辑框获取方法
                break;
            case R.id.register_validate_code_button:
                initGetVaildateCode();//获取验证码
                break;
        }
    }

    /**
     * 验证码获取
     */
    private void initGetVaildateCode() {

        String register_find_phoneNumber_Ed = register_find_phoneNumber.getText().toString();
        if (!TextUtils.isEmpty(register_find_phoneNumber_Ed)) {
            OkHttpUtil.okhttputil_validate(url + "LoginTest2/register1.action", register_find_phoneNumber_Ed, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(NET_ERROR);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData_Vaildate = response.body().string();//传回验证码
                    editor.putString("responseData_Vaildate", responseData_Vaildate);//储存验证码
                    editor.apply();
                }
            });
        } else {
            Toast.makeText(Register.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * 注册框中内容判断
     */

    private void checkRegisterMessage() {

        String register_account_Ed = pref.getString("register_account_Ed", "");
        String register_password_Ed = pref.getString("register_password_Ed", "");
        String register_password_again_Ed = pref.getString("register_password_again_Ed", "");
        String register_find_phoneNumber_Ed = pref.getString("register_find_phoneNumber_Ed", "");
        String register_validate_code_Ed = pref.getString("register_validate_code_Ed", "");
        String responseData_Vaildate = pref.getString("responseData_Vaildate", "");

        if (TextUtils.isEmpty(register_account_Ed)) {
            Toast.makeText(Register.this, "请输入账号", Toast.LENGTH_SHORT).show();
        } else {
            if (TextUtils.isEmpty(register_password_Ed)) {
                Toast.makeText(Register.this, "请输入密码", Toast.LENGTH_SHORT).show();
            } else {
                if (!register_password_Ed.equals(register_password_again_Ed)) {
                    Toast.makeText(Register.this, "请确认两次密码的一致性", Toast.LENGTH_SHORT).show();
                } else {
                    if (TextUtils.isEmpty(register_find_phoneNumber_Ed)) {
                        Toast.makeText(Register.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                    } else {
                        if (TextUtils.isEmpty(register_validate_code_Ed)) {
                            Toast.makeText(Register.this, "请输入验证码", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!register_validate_code_Ed.equals(responseData_Vaildate)) {
                                Toast.makeText(Register.this, "验证码不正确请重新输入", Toast.LENGTH_SHORT).show();
                            } else {
                                submit();//提交数据
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 提交注册数据
     */
    private void submit() {
        String register_account_Ed = pref.getString("register_account_Ed", "");
        String register_password_Ed = pref.getString("register_password_Ed", "");
        String register_find_phoneNumber_Ed = pref.getString("register_find_phoneNumber_Ed", "");
        OkHttpUtil.okhttputil_register(url + "LoginTest2/register.action", register_account_Ed, register_password_Ed, register_find_phoneNumber_Ed, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(NET_ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData_Register = response.body().string();//传回注册数据
                if (responseData_Register.equals("success")) {
                    handler.sendEmptyMessage(REGIRSTER_SUCCESS);
                }
                if (responseData_Register.equals("register_fail")) {
                    handler.sendEmptyMessage(REGIRSTER_FAIL);
                }
            }
        });
    }

}
