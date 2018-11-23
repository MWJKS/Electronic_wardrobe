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
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.Login_Register.MainActivity;
import com.example.a10767.electronic_wardrobe.OkHttpUtil;
import com.example.a10767.electronic_wardrobe.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.a10767.electronic_wardrobe.Fix_Message.ForgetPassword.phone;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;

/**
 * Created by 10767 on 2018/6/24.
 */

/**
 * 更改密码
 */
public class ChangePassword extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ChangePassword";
    EditText change_password;
    EditText change_password_again;
    Button change_button;


    private final int CHANGEPASSWORDSUCCESS = 1;
    private final int NET_ERROR = -1;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CHANGEPASSWORDSUCCESS:
                    Toast.makeText(ChangePassword.this, "修改成功", Toast.LENGTH_SHORT).show();
                    Intent MainActivity_intent = new Intent(ChangePassword.this, MainActivity.class);
                    startActivity(MainActivity_intent);
                    break;
                case NET_ERROR:
                    Toast.makeText(ChangePassword.this, "网络连接异常", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
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
        change_button.setOnClickListener(this);
    }

    private void initUI() {
        change_password = findViewById(R.id.change_password);//修改的密码
        change_password_again = findViewById(R.id.change_password_again);//确认修改的密码
        change_button = findViewById(R.id.change_finish);//完成
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_finish:
                submit_finish();
                break;
        }
    }

    /**
     * 提交完成
     */
    private void submit_finish() {
        String change_password_Ed = change_password.getText().toString();
        String change_password_again_Ed = change_password_again.getText().toString();
        if ((TextUtils.isEmpty(change_password_Ed) && TextUtils.isEmpty(change_password_again_Ed))) {
            Toast.makeText(ChangePassword.this, "密码不能为空", Toast.LENGTH_SHORT).show();
        } else {
            if (!change_password_Ed.equals(change_password_again_Ed)) {
                Toast.makeText(ChangePassword.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            } else {
                OkHttpUtil.okhttputil_change_Password(url + "LoginTest2/updateinfor1.action", phone, change_password_Ed, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        handler.sendEmptyMessage(NET_ERROR);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        handler.sendEmptyMessage(CHANGEPASSWORDSUCCESS);
                        Log.d(TAG, phone);
                    }
                });
            }
        }
    }
}
