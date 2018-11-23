package com.example.a10767.electronic_wardrobe.Main_Fragment;

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
import com.example.a10767.electronic_wardrobe.Clothes_Manage.Clothes_Check;
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

public class HeartLayoutActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout heart_return;
    private Button heart_fix;
    private ProgressDialog progressDialog; //等待
    private static final String TAG = "HeartLayoutActivity";
    private Bitmap bitmap;
    private ImageView heart_IMG;
    private TextView heart_TEXT;
    private String heart_TEXT_ED = "";
    private final int SHOW_ERROR = -1;
    private final int SHOW_SUCCESS = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_ERROR:
                    progressDialog.dismiss();
                    Toast.makeText(HeartLayoutActivity.this, "网络错误，加载失败", Toast.LENGTH_SHORT).show();
                    break;
                case SHOW_SUCCESS:
                    heart_TEXT.setText(heart_TEXT_ED);
                    heart_IMG.setImageBitmap(bitmap);
                    progressDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.heart_layout);
        initUI();
        initView();
        receiveMessage();
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initView() {
        heart_return.setOnClickListener(this);
        heart_fix.setOnClickListener(this);
    }

    private void initUI() {
        progressDialog = new ProgressDialog(HeartLayoutActivity.this);
        progressDialog.setMessage("加载中....");
        progressDialog.setCancelable(false);
        heart_return = findViewById(R.id.heart_return);
        heart_TEXT = findViewById(R.id.heart_TEXT);
        heart_IMG = findViewById(R.id.heart_IMG);
        heart_fix = findViewById(R.id.heart_fix);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(HeartLayoutActivity.this, MyselfCollect.class));
        overridePendingTransition(R.anim.enter2, R.anim.quit2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.heart_return:
                startActivity(new Intent(HeartLayoutActivity.this, MyselfCollect.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.heart_fix:
                startActivity(new Intent(HeartLayoutActivity.this, HeartFix.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
        }
    }


    private void receiveMessage() {
        progressDialog.show();
        OkHttpUtil.postJson_DeleteClothes(url + "LoginTest2/findCollecInformation.action", login_account_Ed, String.valueOf(clo_id), new Callback() {
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
                    heart_TEXT_ED = jsonObject.getString("hearttext");
                    bitmap = getBitmap(jsonObject.getString("othersurl"));
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
