package com.example.a10767.electronic_wardrobe.Clothes_Manage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Accessories;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Bag;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Cap;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Coat;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Jacket;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Pants;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Shoe;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Skirt;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Suit;
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

import static com.example.a10767.electronic_wardrobe.StaticVariable.checkOrSearch;
import static com.example.a10767.electronic_wardrobe.StaticVariable.clo_id;
import static com.example.a10767.electronic_wardrobe.StaticVariable.clo_name;
import static com.example.a10767.electronic_wardrobe.StaticVariable.login_account_Ed;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;

/**
 * Created by 10767 on 2018/7/30.
 */

public class Clothes_Check extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Clothes_Check";
    private ImageView clothes_check_IMG; //图片
    private Bitmap bitmap;
    private TextView clothes_check_color; //颜色
    private String clothes_check_color_Ed = "";
    private TextView clothes_check_style; //风格
    private String clothes_check_style_Ed = "";
    private TextView clothes_check_season; //季节
    private String clothes_check_season_Ed = "";
    private TextView clothes_check_weather; //天气
    private String clothes_check_weather_Ed = "";
    private TextView clothes_check_text; //随笔
    private String clothes_check_text_Ed = "";
    private LinearLayout clothes_check_return; //返回
    private Button clothes_check_fix; //修改

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
                    Toast.makeText(Clothes_Check.this, "网络错误，加载失败", Toast.LENGTH_SHORT).show();
                    break;
                case SHOW_SUCCESS:
                    clothes_check_color.setText(clothes_check_color_Ed);
                    clothes_check_style.setText(clothes_check_style_Ed);
                    clothes_check_season.setText(clothes_check_season_Ed);
                    clothes_check_weather.setText(clothes_check_weather_Ed);
                    clothes_check_text.setText(clothes_check_text_Ed);
                    clothes_check_IMG.setImageBitmap(bitmap);
                    progressDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clothe_check);
        initUI();
        initView();
        uploadMessage();//接受数据
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
    /**
     * 接受数据
     */
    private void uploadMessage() {
        progressDialog.show();
        OkHttpUtil.postJson_DeleteClothes(url + "LoginTest2/findOneUserClothes.action", login_account_Ed, String.valueOf(clo_id), new Callback() {
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
                    clothes_check_color_Ed = jsonObject.getString("cloColor");
                    clothes_check_style_Ed = jsonObject.getString("cloStyle");
                    clothes_check_season_Ed = jsonObject.getString("cloSeason");
                    clothes_check_weather_Ed = jsonObject.getString("cloWeather");
                    clothes_check_text_Ed = jsonObject.getString("cloLabel");
                    bitmap = getBitmap(jsonObject.getString("cloPicture"));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (checkOrSearch.equals("check")) {
            if (clo_name.equals("上衣")) {
                startActivity(new Intent(Clothes_Check.this, Clothe_Jacket.class));
            } else if (clo_name.equals("裙子")) {
                startActivity(new Intent(Clothes_Check.this, Clothe_Skirt.class));
            } else if (clo_name.equals("裤子")) {
                startActivity(new Intent(Clothes_Check.this, Clothe_Pants.class));
            } else if (clo_name.equals("鞋子")) {
                startActivity(new Intent(Clothes_Check.this, Clothe_Shoe.class));
            } else if (clo_name.equals("套装")) {
                startActivity(new Intent(Clothes_Check.this, Clothe_Suit.class));
            } else if (clo_name.equals("包")) {
                startActivity(new Intent(Clothes_Check.this, Clothe_Bag.class));
            } else if (clo_name.equals("外套")) {
                startActivity(new Intent(Clothes_Check.this, Clothe_Coat.class));
            } else if (clo_name.equals("帽子")) {
                startActivity(new Intent(Clothes_Check.this, Clothe_Cap.class));
            } else {
                startActivity(new Intent(Clothes_Check.this, Clothe_Accessories.class));
            }
            overridePendingTransition(R.anim.enter2, R.anim.quit2);
        }
        if (checkOrSearch.equals("search")) {
            checkOrSearch = "";
            startActivity(new Intent(Clothes_Check.this, Clothes_Search.class));
            overridePendingTransition(R.anim.enter2, R.anim.quit2);
        }
    }

    private void initView() {
        clothes_check_return.setOnClickListener(this);
        clothes_check_fix.setOnClickListener(this);
    }

    private void initUI() {
        progressDialog = new ProgressDialog(Clothes_Check.this);
        progressDialog.setMessage("加载中....");
        progressDialog.setCancelable(false);
        clothes_check_fix = findViewById(R.id.clothes_check_fix);
        clothes_check_IMG = findViewById(R.id.clothes_check_IMG);
        clothes_check_color = findViewById(R.id.clothes_check_color);
        clothes_check_season = findViewById(R.id.clothes_check_season);
        clothes_check_style = findViewById(R.id.clothes_check_style);
        clothes_check_weather = findViewById(R.id.clothes_check_weather);
        clothes_check_text = findViewById(R.id.clothes_check_text);
        clothes_check_return = findViewById(R.id.clothes_check_return);
        clothes_check_return = findViewById(R.id.clothes_check_return);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clothes_check_return:
                if (checkOrSearch.equals("search")) {
                    checkOrSearch = "";
                    startActivity(new Intent(Clothes_Check.this, Clothes_Search.class));
                    overridePendingTransition(R.anim.enter2, R.anim.quit2);
                }
                if (checkOrSearch.equals("check")) {
                    if (clo_name.equals("上衣")) {
                        startActivity(new Intent(Clothes_Check.this, Clothe_Jacket.class));
                    } else if (clo_name.equals("裙子")) {
                        startActivity(new Intent(Clothes_Check.this, Clothe_Skirt.class));
                    } else if (clo_name.equals("裤子")) {
                        startActivity(new Intent(Clothes_Check.this, Clothe_Pants.class));
                    } else if (clo_name.equals("鞋子")) {
                        startActivity(new Intent(Clothes_Check.this, Clothe_Shoe.class));
                    } else if (clo_name.equals("套装")) {
                        startActivity(new Intent(Clothes_Check.this, Clothe_Suit.class));
                    } else if (clo_name.equals("包")) {
                        startActivity(new Intent(Clothes_Check.this, Clothe_Bag.class));
                    } else if (clo_name.equals("外套")) {
                        startActivity(new Intent(Clothes_Check.this, Clothe_Coat.class));
                    } else if (clo_name.equals("帽子")) {
                        startActivity(new Intent(Clothes_Check.this, Clothe_Cap.class));
                    } else {
                        startActivity(new Intent(Clothes_Check.this, Clothe_Accessories.class));
                    }
                    overridePendingTransition(R.anim.enter2, R.anim.quit2);
                }
                break;
            case R.id.clothes_check_fix:
                startActivity(new Intent(Clothes_Check.this, Clothes_Fix.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
        }
    }
}
