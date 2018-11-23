package com.example.a10767.electronic_wardrobe.Mix_Manage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.Clothes_Manage.Clothes_Delete;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothes;
import com.example.a10767.electronic_wardrobe.Clothes_Message.ClothesDeleteAdapter;
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

import static com.example.a10767.electronic_wardrobe.StaticVariable.clo_name;
import static com.example.a10767.electronic_wardrobe.StaticVariable.login_account_Ed;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;

public class Mix_Delete extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog progressDialog; //等待
    private Button clothes_delete_deleteButton; //删除
    private Button clothes_delete_choice; //全选
    private LinearLayout clothes_delete_return;
    private ListView delete_mListView;
    Clothes clothes;
    private String state = "";
    private int count = 2;
    private List<Clothes> newList = new ArrayList<>();
    ClothesDeleteAdapter clothesDeleteAdapter;
    private String mix_delete_url;
    private static final String TAG = "Mix_Delete";
    private final int SUCCESS_SHOW = 1;
    private final int ERROR_SHOW = -1;
    private final int DELETE_SUCCESS = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS_SHOW:
                    new NewsAsyncTask().execute(mix_delete_url);
                    break;
                case ERROR_SHOW:
                    progressDialog.dismiss();
                    Toast.makeText(Mix_Delete.this, "网络连接错误", Toast.LENGTH_SHORT).show();
                    break;
                case DELETE_SUCCESS:
                    progressDialog.dismiss();
                    Toast.makeText(Mix_Delete.this, "删除成功", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clothes_delete);
        initUI();
        initView();
        receiveClothes(); //接受衣物地址
        ActivityCollector.addActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Mix_Delete.this, My_Mix.class));
        overridePendingTransition(R.anim.enter2, R.anim.quit2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initUI() {
        progressDialog = new ProgressDialog(Mix_Delete.this);
        progressDialog.setMessage("加载中....");
        progressDialog.setCancelable(false);
        clothesDeleteAdapter = new ClothesDeleteAdapter(Mix_Delete.this, newList);
        delete_mListView = findViewById(R.id.clothes_delete_listView);
        clothes_delete_return = findViewById(R.id.clothes_delete_return);
        clothes_delete_deleteButton = findViewById(R.id.clothes_delete_deleteButton);
        clothes_delete_choice = findViewById(R.id.clothes_delete_choice);
    }

    private void initView() {
        clothes_delete_return.setOnClickListener(this);
        clothes_delete_deleteButton.setOnClickListener(this);
        clothes_delete_choice.setOnClickListener(this);
    }

    private void receiveClothes() {
        progressDialog.show();
        OkHttpUtil.postJson_showMessage(url + "LoginTest2/findComboBegin1.action", login_account_Ed, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(ERROR_SHOW);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mix_delete_url = response.body().string();
                Log.d(TAG, "地址:" + mix_delete_url);
                handler.sendEmptyMessage(SUCCESS_SHOW);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clothes_delete_return:
                startActivity(new Intent(Mix_Delete.this, My_Mix.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.clothes_delete_deleteButton:
                for (int i = newList.size() - 1; i >= 0; i--) {
                    if (newList.get(i).isSelected()) {//如果选中的话
                        state = state + String.valueOf(newList.get(i).getCloId());
                        state = state + ",";
                        newList.remove(i);//如果选中了就将这个移除
                    }
                }
                clothesDeleteAdapter.notifyDataSetChanged();//唤醒数据更新
                uploadMessage(state);
            case R.id.clothes_delete_choice:
                if (count % 2 == 0) {
                    for (int i = newList.size() - 1; i >= 0; i--) {
                        newList.get(i).setSelected(true);
                    }
                } else {
                    for (int i = newList.size() - 1; i >= 0; i--) {
                        newList.get(i).setSelected(false);
                    }
                }
                count++;
                clothesDeleteAdapter.notifyDataSetChanged();//唤醒数据更新
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
            clothesDeleteAdapter = new ClothesDeleteAdapter(Mix_Delete.this, clothes);
            delete_mListView.setAdapter(clothesDeleteAdapter);
            progressDialog.dismiss();
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
            JSONArray jsonArray = jsonObject.getJSONArray("combo");//大集合名字
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                clothes = new Clothes();
/*===============================接受数据======================================================*/
                clothes.setCloId(jsonObject.getInt("clomatchid"));
                clothes.setPicture(jsonObject.getString("clomatchpicture"));
                clothes.setClo_label(jsonObject.getString("clomatchlabel"));
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
     * 上传删除数据
     *
     * @param state
     */
    private void uploadMessage(String state) {
        progressDialog.show();
        String cloId = state;
        Log.d(TAG, cloId);
        OkHttpUtil.postJson_DeleteClothes(url + "LoginTest2/deleteCombo.action", login_account_Ed, cloId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(ERROR_SHOW);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handler.sendEmptyMessage(DELETE_SUCCESS);
            }
        });
    }
}
