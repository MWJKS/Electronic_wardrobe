package com.example.a10767.electronic_wardrobe.Mix_Manage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Jacket;
import com.example.a10767.electronic_wardrobe.OkHttpUtil;
import com.example.a10767.electronic_wardrobe.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.a10767.electronic_wardrobe.StaticVariable.clo_id;
import static com.example.a10767.electronic_wardrobe.StaticVariable.login_account_Ed;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;

public class Mix_Check extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Mix_Check";
    private ImageView mix_check_IMG; //图片
    private Bitmap bitmap;
    private TextView mix_check_scene; //颜色
    private String mix_check_scene_Ed = "";
    private TextView mix_check_style; //风格
    private String mix_check_style_Ed = "";
    private TextView mix_check_season; //季节
    private String mix_check_season_Ed = "";
    private TextView mix_check_weather; //天气
    private String mix_check_weather_Ed = "";
    private TextView mix_check_text; //随笔
    private String mix_check_text_Ed = "";
    private LinearLayout mix_check_return; //返回
    private Button mix_check_fix; //修改

    private ProgressDialog progressDialog; //等待

    private final int SHOW_ERROR = -1;
    private final int SHOW_SUCCESS = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_ERROR:
                    progressDialog.dismiss();
                    Toast.makeText(Mix_Check.this, "网络错误，加载失败", Toast.LENGTH_SHORT).show();
                    break;
                case SHOW_SUCCESS:
                    mix_check_scene.setText(mix_check_scene_Ed);
                    mix_check_style.setText(mix_check_style_Ed);
                    mix_check_season.setText(mix_check_season_Ed);
                    mix_check_weather.setText(mix_check_weather_Ed);
                    mix_check_text.setText(mix_check_text_Ed);
                    mix_check_IMG.setImageBitmap(bitmap);
                    progressDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mix_check);
        initUI();
        initView();
        receiveClothes(); //接受衣物地址
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Mix_Check.this, My_Mix.class));
        overridePendingTransition(R.anim.enter2, R.anim.quit2);
    }

    private void initView() {
        mix_check_return.setOnClickListener(this);
        mix_check_fix.setOnClickListener(this);
    }

    private void initUI() {
        progressDialog = new ProgressDialog(Mix_Check.this);
        progressDialog.setMessage("加载中....");
        progressDialog.setCancelable(false);
        mix_check_fix = findViewById(R.id.mix_check_fix);
        mix_check_IMG = findViewById(R.id.mix_check_IMG);
        mix_check_scene = findViewById(R.id.mix_check_scene);
        mix_check_season = findViewById(R.id.mix_check_season);
        mix_check_style = findViewById(R.id.mix_check_style);
        mix_check_weather = findViewById(R.id.mix_check_weather);
        mix_check_text = findViewById(R.id.mix_check_text);
        mix_check_return = findViewById(R.id.mix_check_return);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mix_check_return:
                startActivity(new Intent(Mix_Check.this, My_Mix.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.mix_check_fix:
               startActivity(new Intent(Mix_Check.this,Mix_Fix.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
        }
    }

    private void receiveClothes() {
        progressDialog.show();
        OkHttpUtil.postJson_DeleteClothes(url + "LoginTest2/findComInformation.action", login_account_Ed,String.valueOf(clo_id), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(SHOW_ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String show_message = response.body().string();
                Log.d(TAG, show_message);
                try {
                    JSONObject jsonObject = new JSONObject(show_message);
                    mix_check_scene_Ed = jsonObject.getString("clomatchoccasion");
                    mix_check_style_Ed = jsonObject.getString("clomatchstyle");
                    mix_check_season_Ed = jsonObject.getString("clomatchseason");
                    mix_check_weather_Ed = jsonObject.getString("clomatchweather");
                    mix_check_text_Ed = jsonObject.getString("clomatchlabel");
                    bitmap = getBitmap(jsonObject.getString("clomatchpicture"));
                    handler.sendEmptyMessage(SHOW_SUCCESS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 获取图片
     *
     * @param path
     * @return
     * @throws IOException
     */
    private Bitmap getBitmap(String path) throws IOException {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }
        return null;
    }
}
