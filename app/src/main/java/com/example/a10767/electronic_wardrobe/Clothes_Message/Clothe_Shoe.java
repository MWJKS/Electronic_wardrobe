package com.example.a10767.electronic_wardrobe.Clothes_Message;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.example.a10767.electronic_wardrobe.Clothes_Manage.Clothes_Check;
import com.example.a10767.electronic_wardrobe.Clothes_Manage.Clothes_Delete;
import com.example.a10767.electronic_wardrobe.Clothes_Manage.Clothes_Search;
import com.example.a10767.electronic_wardrobe.Clothes_Manage.Shoe_Add;
import com.example.a10767.electronic_wardrobe.Main_Fragment.TheMain;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.a10767.electronic_wardrobe.StaticVariable.checkOrSearch;
import static com.example.a10767.electronic_wardrobe.StaticVariable.clo_id;
import static com.example.a10767.electronic_wardrobe.StaticVariable.login_account_Ed;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;
import static com.example.a10767.electronic_wardrobe.StaticVariable.themain;
import static com.example.a10767.electronic_wardrobe.StaticVariable.clo_name;

/**
 * Created by 10767 on 2018/7/19.
 */

public class Clothe_Shoe extends AppCompatActivity implements View.OnClickListener {
    private ListView mListView;
    private String shoe_url; //地址
    private static final String TAG = "Clothe_Jacket";
    private LinearLayout shoe_return; //返回
    private Button shoe_add; //添加
    private ProgressDialog progressDialog; //等待
    private Button shoe_manage; //管理
    private Button shoe_search; //查询
    private final int SUCCESS_SHOW = 1;
    private final int ERROR_SHOW = -1;
    private Clothes clothes;
    private List<Clothes> newList = new ArrayList<>();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS_SHOW:
                    new NewsAsyncTask().execute(shoe_url);
                    break;
                case ERROR_SHOW:
                    Toast.makeText(Clothe_Shoe.this, "网络连接错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clothe_shoe);
        mListView = findViewById(R.id.shoe_listView);
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
        themain = 1;
        startActivity(new Intent(Clothe_Shoe.this, TheMain.class));
        overridePendingTransition(R.anim.enter2, R.anim.quit2);
    }

    private void initView() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                checkOrSearch = "check";
                clothes = newList.get(i);
                clo_id = clothes.getCloId();
                Log.d(TAG, String.valueOf(clo_id));
                startActivity(new Intent(Clothe_Shoe.this, Clothes_Check.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
            }
        });
        shoe_search.setOnClickListener(this);
        shoe_manage.setOnClickListener(this);
        shoe_return.setOnClickListener(this);
        shoe_add.setOnClickListener(this);
    }

    private void initUI() {
        clo_name = "鞋子";
        progressDialog = new ProgressDialog(Clothe_Shoe.this);
        progressDialog.setMessage("加载中....");
        progressDialog.setCancelable(false);
        shoe_search=findViewById(R.id.shoe_search);
        shoe_manage = findViewById(R.id.shoe_manage);
        shoe_add = findViewById(R.id.shoe_add);
        shoe_return = findViewById(R.id.shoe_return);
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
                clothes.setClo_collect(jsonObject.getBoolean("othercollect"));
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shoe_add:
                startActivity(new Intent(Clothe_Shoe.this, Shoe_Add.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.shoe_return:
                themain = 1;
                startActivity(new Intent(Clothe_Shoe.this, TheMain.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.shoe_manage:
                startActivity(new Intent(Clothe_Shoe.this, Clothes_Delete.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.shoe_search:
                startActivity(new Intent(Clothe_Shoe.this, Clothes_Search.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
        }
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
        protected void onPostExecute(List<Clothes> clothes) {
            super.onPostExecute(clothes);
            progressDialog.dismiss();
            ClothesAdapter clothesAdapter = new ClothesAdapter(Clothe_Shoe.this, clothes);
            mListView.setAdapter(clothesAdapter);
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
                shoe_url = response.body().string();
                Log.d(TAG, "地址:" + shoe_url);
                handler.sendEmptyMessage(SUCCESS_SHOW);
            }
        });

    }

}
