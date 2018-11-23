package com.example.a10767.electronic_wardrobe.Mix_Manage;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;


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
import static com.example.a10767.electronic_wardrobe.StaticVariable.search_content;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;

/**
 * Created by 10767 on 2018/7/31.
 */

public class MixSearchFragment extends Fragment {
    private static final String TAG = "MixSearchFragment";
    private ProgressDialog progressDialog; //等待
    private String search_url; //地址
    private Make_Up make_up;
    private List<Make_Up> newList = new ArrayList<>();
    private ListView mListView;

    private final int SUCCESS_SHOW = 1;
    private final int ERROR_SHOW = -1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS_SHOW:
                    new NewsAsyncTask().execute(search_url);
                    break;
                case ERROR_SHOW:
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "网络连接错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.clothe_search_listview, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
        initView();
        receiveClothes();

    }

    private void initView() {
        progressDialog.setCancelable(false);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                checkOrSearch = "search";
//                clothes = newList.get(i);
//                clo_id = clothes.getCloId();
//                Log.d(TAG, String.valueOf(clo_id));
//                startActivity(new Intent(getContext(), Clothes_Check.class));
//                getActivity().overridePendingTransition(R.anim.enter, R.anim.quit);
//            }
//        });
    }

    private void initUI() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("加载中....");
        mListView = getActivity().findViewById(R.id.clothe_search_listView);
        Log.d(TAG, search_content);
    }

    /**
     * 接受地址
     */
    private void receiveClothes() {
        progressDialog.show();
          /*接受数据*/
        OkHttpUtil.postJson_MixSearchClothes(url + "LoginTest2/findCombo1.action", login_account_Ed, search_content, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(ERROR_SHOW);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                search_url = response.body().string();
                Log.d(TAG, "地址:" + search_url);
                handler.sendEmptyMessage(SUCCESS_SHOW);
            }
        });
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
            Make_UpAdapter make_upAdapter = new Make_UpAdapter(getContext(), list);
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
/*===================================接受数据======================================================*/
                make_up.setMixWeather(jsonObject.getString("clomatchweather"));
                make_up.setMixCollect(jsonObject.getBoolean("othercollect"));
                make_up.setMixStyle(jsonObject.getString("clomatchstyle"));
                make_up.setMixPicture(jsonObject.getString("clomatchpicture"));
                make_up.setMixSeason(jsonObject.getString("clomatchseason"));
                make_up.setMixOccasion(jsonObject.getString("clomatchoccasion"));
                make_up.setMixLabel(jsonObject.getString("clomatchlabel"));
                make_up.setMixId(jsonObject.getInt("clomatchid"));

                Log.d(TAG, "CloId" + jsonObject.getInt("clomatchid")
                        + "图片地址:" + jsonObject.getString("clomatchpicture")
                        + "便签:" + jsonObject.getString("clomatchlabel")
                        + "颜色:" + jsonObject.getString("clomatchoccasion")
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
}
