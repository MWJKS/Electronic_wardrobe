package com.example.a10767.electronic_wardrobe.Main_Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.Main_Fragment.TheMain;
import com.example.a10767.electronic_wardrobe.Mix_Manage.Frame_All;
import com.example.a10767.electronic_wardrobe.Mix_Manage.Mix;
import com.example.a10767.electronic_wardrobe.Mix_Manage.Mix_Jacket;
import com.example.a10767.electronic_wardrobe.OkHttpUtil;
import com.example.a10767.electronic_wardrobe.Others_Wardrobe.Others;
import com.example.a10767.electronic_wardrobe.Others_Wardrobe.OthersAdapter;
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
import static com.example.a10767.electronic_wardrobe.StaticVariable.mixFrameList;
import static com.example.a10767.electronic_wardrobe.StaticVariable.mixPosition;
import static com.example.a10767.electronic_wardrobe.StaticVariable.othersBitmap;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;

/**
 * Created by 10767 on 2018/8/14.
 */

public class MyselfCollect extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MyselfCollect";
    private LinearLayout myself_collect_return;
    private Button myself_collect_search;
    private ProgressDialog progressDialog; //等待
    private ListView mListView;
    private String receiveOthersUrl;
    private String id;
    private List<Collect> newList = new ArrayList<>();
    private Collect collect;
    private CollectAdapter collectAdapter;
    private final int RECEIVE_SUCCESS = 1;
    private final int RECEIVE_FAIL = -1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RECEIVE_SUCCESS:
                    Log.d(TAG, receiveOthersUrl);
                    new NewsAsyncTask().execute(receiveOthersUrl);
                    break;
                case RECEIVE_FAIL:
                    progressDialog.dismiss();
                    Toast.makeText(MyselfCollect.this, "网络连接错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myself_collect);
        initUI();
        initView();
        receiveMessageUrl(); // 接受信息地址
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initView() {
        myself_collect_return.setOnClickListener(this);
//        myself_collect_search.setOnClickListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                collect = newList.get(i);
                clo_id = Integer.parseInt(collect.getId());
                startActivity(new Intent(MyselfCollect.this, HeartLayoutActivity.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
            }
        });

    }

    private void initUI() {
        progressDialog = new ProgressDialog(MyselfCollect.this);
        progressDialog.setMessage("加载中....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        collectAdapter = new CollectAdapter(MyselfCollect.this, newList);
        mListView = findViewById(R.id.myself_collect_listView);
        myself_collect_return = findViewById(R.id.myself_collect_return);
//        myself_collect_add = findViewById(R.id.myself_collect_search);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MyselfCollect.this, TheMain.class));
        overridePendingTransition(R.anim.enter2, R.anim.quit2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.myself_collect_return:
                startActivity(new Intent(MyselfCollect.this, TheMain.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
//            case R.id.myself_collect_search:
//                startActivity(new Intent(MyselfCollect.this, MyselfCollectSearch.class));
//                overridePendingTransition(R.anim.enter, R.anim.quit);
//                break;
        }
    }


    /**
     * 将url对应的JSON格式数据穿化成我们锁封装NewsBean
     *
     * @param strings
     * @return
     */
    private List<Collect> getJasonDate(String strings) {
        try {
            String jsonString = readStream(new URL(strings).openStream());
            //根据地址获取数据，返回的类型为InputStream
            JSONObject jsonObject;
            jsonObject = new JSONObject(jsonString);

            JSONArray jsonArray = jsonObject.getJSONArray("coll");//大集合名字
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                collect = new Collect();
/*===============================接受数据======================================================*/
                collect.setCollect(jsonObject.getBoolean("othercollect"));
                collect.setPicture(jsonObject.getString("othersurl"));
                collect.setTxt(jsonObject.getString("othertext"));
                collect.setId(jsonObject.getString("collectid"));
                newList.add(collect);
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
     * 实现网络的异步访问
     */
    class NewsAsyncTask extends AsyncTask<String, Void, List<Collect>> {
        /**
         * 每一个List都代表一行数据
         *
         * @param strings 请求的网址
         * @return
         */
        @Override
        protected List<Collect> doInBackground(String... strings) {
            return getJasonDate(strings[0]);
        }

        @Override
        protected void onPostExecute(final List<Collect> list) {
            super.onPostExecute(list);
            collectAdapter = new CollectAdapter(MyselfCollect.this, list);
            progressDialog.dismiss();
            mListView.setAdapter(collectAdapter);

        }
    }

    /**
     * 接受网址
     */
    private void receiveMessageUrl() {
        OkHttpUtil.postJson_OthersClothes(url + "LoginTest2/findCollection1.action", login_account_Ed, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(RECEIVE_FAIL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                receiveOthersUrl = response.body().string();
                Log.d(TAG, "地址" + receiveOthersUrl);
                handler.sendEmptyMessage(RECEIVE_SUCCESS);
            }
        });
    }

}
