package com.example.a10767.electronic_wardrobe.Others_Wardrobe;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
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

import static com.example.a10767.electronic_wardrobe.StaticVariable.login_account_Ed;
import static com.example.a10767.electronic_wardrobe.StaticVariable.othersBitmap;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;

/**
 * Created by 10767 on 2018/8/14.
 */

public class OthersWardrobe extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "OthersWardrobe";
    private LinearLayout others_wardrobe_return;
    private Button others_wardrobe_add;
    private ProgressDialog progressDialog; //等待
    private ListView mListView;
    private String receiveOthersUrl;
    private List<Others> newList = new ArrayList<>();
    private Others others;
    private OthersAdapter othersAdapter;
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
                    Toast.makeText(OthersWardrobe.this, "网络连接错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.others_wardrobe);
        initUI();
        initView();
        ActivityCollector.addActivity(this);
        receiveMessageUrl(); // 接受信息地址
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(OthersWardrobe.this, TheMain.class));
        overridePendingTransition(R.anim.enter2, R.anim.quit2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initView() {
        others_wardrobe_return.setOnClickListener(this);
        others_wardrobe_add.setOnClickListener(this);
    }

    private void initUI() {
        progressDialog = new ProgressDialog(OthersWardrobe.this);
        progressDialog.setMessage("加载中....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        othersAdapter = new OthersAdapter(OthersWardrobe.this, newList);
        mListView = findViewById(R.id.others_wardrobe_listView);
        others_wardrobe_return = findViewById(R.id.others_wardrobe_return);
        others_wardrobe_add = findViewById(R.id.others_wardrobe_add);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.others_wardrobe_return:
                startActivity(new Intent(OthersWardrobe.this, TheMain.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.others_wardrobe_add:
                othersBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.others_add); //添加默认图片（加号）
                startActivity(new Intent(OthersWardrobe.this, Others_Add.class));
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
        }
    }


    /**
     * 将url对应的JSON格式数据穿化成我们锁封装NewsBean
     *
     * @param strings
     * @return
     */
    private List<Others> getJasonDate(String strings) {
        try {
            String jsonString = readStream(new URL(strings).openStream());
            //根据地址获取数据，返回的类型为InputStream
            JSONObject jsonObject;
            jsonObject = new JSONObject(jsonString);

            JSONArray jsonArray = jsonObject.getJSONArray("other");//大集合名字
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                others = new Others();
/*===============================接受数据======================================================*/
                others.setName(jsonObject.getString("useralias"));
                others.setTime(jsonObject.getString("othertime"));
                others.setNumber(String.valueOf(jsonObject.getInt("othernumber")));
                others.setContentText(jsonObject.getString("othertext"));
                others.setHeadPicture(jsonObject.getString("othersheaderpicture"));
                others.setPicture(jsonObject.getString("otherpicture"));
                others.setCollectId(jsonObject.getBoolean("othercollect"));
//                others.setColour(jsonObject.getString("othercolour"));
//                others.setSeason(jsonObject.getString("otherseason"));
//                others.setStyle(jsonObject.getString("otherstyle"));
//                others.setWeather(jsonObject.getString("otherweather"));
                Log.d(TAG, "头像地址:" + jsonObject.getString("othersheaderpicture")
                        + "账号名称:" + jsonObject.getString("username")
                        + "时间:" + jsonObject.getString("othertime")
                        + "序号:" + jsonObject.getInt("othernumber")
//                        + "色调:" + jsonObject.getString("othercolour")
//                        + "季节:" + jsonObject.getString("otherseason")
//                        + "风格:" + jsonObject.getString("otherstyle")
//                        + "天气:" + jsonObject.getString("otherweather")
                        + "图片地址:" + jsonObject.getString("otherpicture")
                        + "收藏标识：" + jsonObject.getBoolean("othercollect")
                        + "便签:" + jsonObject.getString("othertext"));
                newList.add(others);
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
    class NewsAsyncTask extends AsyncTask<String, Void, List<Others>> {
        /**
         * 每一个List都代表一行数据
         *
         * @param strings 请求的网址
         * @return
         */
        @Override
        protected List<Others> doInBackground(String... strings) {
            return getJasonDate(strings[0]);
        }

        @Override
        protected void onPostExecute(final List<Others> list) {
            super.onPostExecute(list);
            othersAdapter = new OthersAdapter(OthersWardrobe.this, list);
            progressDialog.dismiss();
            mListView.setAdapter(othersAdapter);

        }
    }

    /**
     * 接受网址
     */
    private void receiveMessageUrl() {
        OkHttpUtil.postJson_OthersClothes(url+ "LoginTest2/OtherBegin1.action",login_account_Ed ,new Callback() {
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
