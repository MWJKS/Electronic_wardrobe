package com.example.a10767.electronic_wardrobe.Others_Wardrobe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothes;
import com.example.a10767.electronic_wardrobe.Clothes_Message.ClothesAdapter;
import com.example.a10767.electronic_wardrobe.Mix_Manage.Frame_All;
import com.example.a10767.electronic_wardrobe.Mix_Manage.Wardrobe_choice;
import com.example.a10767.electronic_wardrobe.OkHttpUtil;
import com.example.a10767.electronic_wardrobe.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.a10767.electronic_wardrobe.StaticVariable.login_account_Ed;
import static com.example.a10767.electronic_wardrobe.StaticVariable.mixFrameList;
import static com.example.a10767.electronic_wardrobe.StaticVariable.mixPosition;
import static com.example.a10767.electronic_wardrobe.StaticVariable.othersBitmap;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;

/**
 * Created by 10767 on 2018/8/8.
 */


/***
 *  搭配上衣衣柜（他的衣柜中搭配）
 */

public class Others_Shoe extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Mix_Jacket";
    private String jacket_url; //地址
    private ProgressDialog progressDialog; //等待
    private LinearLayout others_shoe_return; //返回
    private Button others_shoe_search; //查找
    private List<Clothes> newList = new ArrayList<>();
    private Clothes clothes;
    private String pictureUrl;
    private ListView mListView;
    private final int SUCCESS_SHOW = 1;
    private final int ERROR_SHOW = -1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS_SHOW:
                    new NewsAsyncTask().execute(jacket_url);
                    break;
                case ERROR_SHOW:
                    progressDialog.dismiss();
                    Toast.makeText(Others_Shoe.this, "网络连接错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.others_shoe);
        initUI();
        initView();
        ActivityCollector.addActivity(this);
        receiveClothes(); //接受数据

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Others_Shoe.this,Others_Wardrobe_Choice.class));
        overridePendingTransition(R.anim.enter2, R.anim.quit2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initView() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                progressDialog.show();
                clothes = newList.get(i);
                pictureUrl = clothes.getPicture();
                Log.d(TAG, "地址：" + pictureUrl);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            othersBitmap= getBitmap(pictureUrl);
                            startActivity(new Intent(Others_Shoe.this, Others_Add.class));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        others_shoe_search.setOnClickListener(this);
        others_shoe_return.setOnClickListener(this);

    }


    private void initUI() {
        progressDialog = new ProgressDialog(Others_Shoe.this);
        progressDialog.setMessage("加载中....");
        progressDialog.setCancelable(false);
        mListView = findViewById(R.id.others_shoe_listView);
        others_shoe_return = findViewById(R.id.others_shoe_return);
        others_shoe_search = findViewById(R.id.others_shoe_search);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.others_shoe_return:
                startActivity(new Intent(Others_Shoe.this,Others_Wardrobe_Choice.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.others_shoe_search:

                break;
        }
    }

    /**
     * 接受地址
     */
    private void receiveClothes() {
        progressDialog.show();
          /*接受数据*/
        OkHttpUtil.postJson_showClothes(url + "LoginTest2/findUserClothesBegin.action", login_account_Ed, "鞋子", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(ERROR_SHOW);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                jacket_url = response.body().string();
                Log.d(TAG, "地址:" + jacket_url);
                handler.sendEmptyMessage(SUCCESS_SHOW);
            }
        });

    }

    /**
     * 实现网络的异步访问
     */
    class NewsAsyncTask extends AsyncTask<String, Void, List<Clothes>> {
        /**
         * 每一个List都代表一行数据
         *
         * @param strings 请求的网址
         * @return
         */
        @Override
        protected List<Clothes> doInBackground(String... strings) {
            return getJasonDate(strings[0]);
        }

        @Override
        protected void onPostExecute(final List<Clothes> list) {
            super.onPostExecute(list);
            ClothesAdapter clothesAdapter = new ClothesAdapter(Others_Shoe.this, list);
            progressDialog.dismiss();
            mListView.setAdapter(clothesAdapter);

        }
    }


    /**
     * 将url对应的JSON格式数据穿化成我们锁封装NewsBean
     *
     * @param strings
     * @return
     */
    private List<Clothes> getJasonDate(String strings) {
        try {
            String jsonString = readStream(new URL(strings).openStream());
            //根据地址获取数据，返回的类型为InputStream
            JSONObject jsonObject;
            jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("data");//大集合名字
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                clothes = new Clothes();
/*===============================接受数据======================================================*/
                clothes.setCloId(jsonObject.getInt("cloId"));
                clothes.setPicture(jsonObject.getString("cloPicture"));
                clothes.setClo_label(jsonObject.getString("cloLabel"));
                clothes.setClo_color(jsonObject.getString("cloColor"));
                clothes.setSeason(jsonObject.getString("cloSeason"));
                clothes.setStyle(jsonObject.getString("cloStyle"));
                clothes.setWeather(jsonObject.getString("cloWeather"));
                Log.d(TAG, "CloId" + jsonObject.getInt("cloId")
                        + "图片地址:" + jsonObject.getString("cloPicture")
                        + "便签:" + jsonObject.getString("cloLabel")
                        + "颜色:" + jsonObject.getString("cloColor")
                        + "季节:" + jsonObject.getString("cloSeason")
                        + "风格:" + jsonObject.getString("cloStyle")
                        + "天气:" + jsonObject.getString("cloWeather"));
                newList.add(clothes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newList;
    }


    /**
     * 获取信息
     *
     * @param is
     * @return
     */
    private String readStream(InputStream is) {
        InputStreamReader isr;
        String result = "";
        try {
            String line = "";
            isr = new InputStreamReader(is, "utf-8");  //字节流d转化为字符流
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
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
