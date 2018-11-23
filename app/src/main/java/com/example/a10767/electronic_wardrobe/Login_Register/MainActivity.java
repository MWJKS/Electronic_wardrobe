package com.example.a10767.electronic_wardrobe.Login_Register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.Fix_Message.ForgetPassword;
import com.example.a10767.electronic_wardrobe.OkHttpUtil;
import com.example.a10767.electronic_wardrobe.R;
import com.example.a10767.electronic_wardrobe.Main_Fragment.TheMain;
import static com.example.a10767.electronic_wardrobe.StaticVariable.login_account_Ed;
import static com.example.a10767.electronic_wardrobe.StaticVariable.isExit;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.a10767.electronic_wardrobe.StaticVariable.url;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog progressDialog; //等待

    private static final String TAG = "MainActivity";
    private String login_username_ED="";
    private String login_password_ED="";
    EditText login_username;
    EditText login_password;
    Button register_button;
    Button login_button;
    Button forget_password_button;

    private final int LOGIN_SUCCESS = 1;
    private final int NET_ERROR = -1;
    private final int LOGIN_FAIL = 0;
    Handler mHandler2 = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, TheMain.class);
                    startActivity(intent);
                    break;
                case NET_ERROR:
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                    break;
                case LOGIN_FAIL:
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initView();
    }


    /**
     * 初始化监听
     */
    private void initView() {
        progressDialog.setCancelable(false);
        register_button.setOnClickListener(this);
        login_button.setOnClickListener(this);
        forget_password_button.setOnClickListener(this);

    }


    /**
     * 初始化UI
     */
    private void initUI() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("登陆中....");
        login_button = findViewById(R.id.login_button);//登陆按钮
        register_button = findViewById(R.id.register_button);//新用户注册按钮
        forget_password_button = findViewById(R.id.forget_password_button);//忘记密码按钮
        login_username = findViewById(R.id.login_username);//登录账号
        login_password = findViewById(R.id.login_password);//登陆密码

    }

    /**
     * 按钮点击
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_button:
                Intent Register_intent = new Intent(MainActivity.this, Register.class);
                startActivity(Register_intent);
                break;
            case R.id.login_button:
                initEditText(); //编辑框获取方法
                break;
            case R.id.forget_password_button:
                Intent ForgetPassword_intent = new Intent(MainActivity.this, ForgetPassword.class);
                startActivity(ForgetPassword_intent);
                break;
        }
    }


    /**
     * 编辑框获取方法
     */
    private void initEditText() {
        progressDialog.show();
        login_account_Ed = login_username.getText().toString();
        login_username_ED=  login_account_Ed;
        login_password_ED = login_password.getText().toString();
        sendLoginMessage();//发送登陆信息
    }

    /**
     * 发送登录信息
     */
    private void sendLoginMessage() {
        OkHttpUtil.okhttputil_login(url + "LoginTest2/login.action", login_account_Ed, login_password_ED, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(NET_ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData_Login = response.body().string();//传回注册数据
                if (responseData_Login.equals("success")) {
                    handler.sendEmptyMessage(LOGIN_SUCCESS);
                } else
                    handler.sendEmptyMessage(LOGIN_FAIL);
            }
        });
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
            finish();
            System.exit(0);
        }

    }
}