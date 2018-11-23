package com.example.a10767.electronic_wardrobe.Main_Fragment;

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

import com.example.a10767.electronic_wardrobe.Clothes_Manage.SearchFragment;
import com.example.a10767.electronic_wardrobe.OkHttpUtil;
import com.example.a10767.electronic_wardrobe.Others_Wardrobe.Others;
import com.example.a10767.electronic_wardrobe.Others_Wardrobe.OthersAdapter;
import com.example.a10767.electronic_wardrobe.Others_Wardrobe.OthersWardrobe;
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
 * Created by 10767 on 2018/9/10.
 */

public class MyselfCollectionSearchFragment extends Fragment {
    private static final String TAG = "MyselfCollectionSearchF";
    private ListView mListView;
    private ProgressDialog progressDialog; //等待
    private String receiveOthersUrl; //地址
    private Collect collect;
    private List<Collect> newList = new ArrayList<>();

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
//      initView();
        receiveMessageUrl(); // 接受信息地址
    }

    private void initUI() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("加载中....");
        mListView = getActivity().findViewById(R.id.clothe_search_listView);
        Log.d(TAG, search_content);
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
            collectAdapter = new CollectAdapter(getContext(), list);
            progressDialog.dismiss();
            mListView.setAdapter(collectAdapter);

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
                collect.setPicture(jsonObject.getString("otherpicture"));
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
     * 接受网址
     */
    private void receiveMessageUrl() {

    }
}
