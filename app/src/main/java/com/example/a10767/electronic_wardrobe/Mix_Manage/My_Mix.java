package com.example.a10767.electronic_wardrobe.Mix_Manage;

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
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Jacket;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothes;
import com.example.a10767.electronic_wardrobe.Clothes_Message.ClothesAdapter;
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

import static com.example.a10767.electronic_wardrobe.StaticVariable.clo_id;
import static com.example.a10767.electronic_wardrobe.StaticVariable.login_account_Ed;
import static com.example.a10767.electronic_wardrobe.StaticVariable.mix_choice;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;

/**
 * Created by 10767 on 2018/8/9.
 */


/**
 * 我的搭配(目前只能显示，按钮未编辑)
 */
public class My_Mix extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "My_Mix";
    private ProgressDialog progressDialog; //等待
    private String my_mix_url; //地址
    private ListView mListView;
    private Button my_mix_add;
    private Button my_mix_search;//搜索
    private Make_Up make_up;
    private Button my_mix_manage;
    private List<Make_Up> newList = new ArrayList<>();
    private final int SUCCESS_SHOW = 1;
    private final int ERROR_SHOW = -1;

    private LinearLayout my_mix_return;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS_SHOW:
                    new NewsAsyncTask().execute(my_mix_url);
                    break;
                case ERROR_SHOW:
                    progressDialog.dismiss();
                    Toast.makeText(My_Mix.this, "网络连接错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_mix_listview);
        initUI();
        initView();
        ActivityCollector.addActivity(this);
        receiveClothes(); //接受衣物地址

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(My_Mix.this, TheMain.class));
        overridePendingTransition(R.anim.enter2, R.anim.quit2);
    }

    private void initView() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                make_up = newList.get(i);
                clo_id = make_up.getMixId();
                startActivity(new Intent(My_Mix.this, Mix_Check.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
            }
        });
        my_mix_return.setOnClickListener(this);
        my_mix_add.setOnClickListener(this);
        my_mix_manage.setOnClickListener(this);
        my_mix_search.setOnClickListener(this);
    }

    private void initUI() {
        progressDialog = new ProgressDialog(My_Mix.this);
        progressDialog.setMessage("加载中....");
        progressDialog.setCancelable(false);
        mListView = findViewById(R.id.my_mix_listView);
        my_mix_search = findViewById(R.id.my_mix_search);
        my_mix_add = findViewById(R.id.my_mix_add);
        my_mix_manage = findViewById(R.id.my_mix_manage);
        my_mix_return = findViewById(R.id.my_mix_return);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_mix_return:
                startActivity(new Intent(My_Mix.this, TheMain.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.my_mix_add:
                mix_choice = "myMix";
                startActivity(new Intent(My_Mix.this, Clothe_Mix.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.my_mix_search:
                startActivity(new Intent(My_Mix.this, Mix_Search.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.my_mix_manage:
                startActivity(new Intent(My_Mix.this, Mix_Delete.class));
                overridePendingTransition(R.anim.enter,R.anim.quit);
                break;
        }
    }

    /**
     * 实现网络的异步访问
     */
    class NewsAsyncTask extends AsyncTask<String, Void, List<Make_Up>> {
        /**
         * 每一个List都代表一行数据
         *
         * @param strings 请求的网址
         * @return
         */
        @Override
        protected List<Make_Up> doInBackground(String... strings) {
            return getJasonDate(strings[0]);
        }

        @Override
        protected void onPostExecute(final List<Make_Up> list) {
            super.onPostExecute(list);
            Make_UpAdapter make_upAdapter = new Make_UpAdapter(My_Mix.this, list);
            progressDialog.dismiss();
            mListView.setAdapter(make_upAdapter);

        }
    }

    /**
     * 将url对应的JSON格式数据穿化成我们锁封装NewsBean
     *
     * @param strings
     * @return
     */
    private List<Make_Up> getJasonDate(String strings) {
        try {
            String jsonString = readStream(new URL(strings).openStream());
            //根据地址获取数据，返回的类型为InputStream
            JSONObject jsonObject;
            jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("combo");//大集合名字
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                make_up = new Make_Up();
/*===============================接受数据======================================================*/
                make_up.setMixId(jsonObject.getInt("clomatchid"));
                make_up.setMixOccasion(jsonObject.getString("clomatchoccasion"));
                make_up.setMixCollect(jsonObject.getBoolean("othercollect"));
                make_up.setMixSeason(jsonObject.getString("clomatchseason"));
                make_up.setMixLabel(jsonObject.getString("clomatchlabel"));
                make_up.setMixPicture(jsonObject.getString("clomatchpicture"));
                make_up.setMixStyle(jsonObject.getString("clomatchstyle"));
                make_up.setMixWeather(jsonObject.getString("clomatchweather"));
                Log.d(TAG, "CloId" + jsonObject.getInt("clomatchid")
                        + "图片地址:" + jsonObject.getString("clomatchpicture")
                        + "便签:" + jsonObject.getString("clomatchlabel")
                        + "场合:" + jsonObject.getString("clomatchoccasion")
                        + "季节:" + jsonObject.getString("clomatchseason")
                        + "风格:" + jsonObject.getString("clomatchstyle")
                        + "天气:" + jsonObject.getString("clomatchweather"));
                newList.add(make_up);
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
     * 接受衣物地址
     */
    private void receiveClothes() {
        progressDialog.show();
        OkHttpUtil.postJson_showMessage(url + "LoginTest2/findComboBegin1.action", login_account_Ed, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(ERROR_SHOW);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                my_mix_url = response.body().string();
                Log.d(TAG, "地址:" + my_mix_url);
                handler.sendEmptyMessage(SUCCESS_SHOW);
            }
        });
    }
}
