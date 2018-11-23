package com.example.a10767.electronic_wardrobe.Mix_Manage;

/**
 * Created by 10767 on 2018/8/9.
 */


import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.AtimationTools;
import com.example.a10767.electronic_wardrobe.Clothes_Message.ImageLoader;
import com.example.a10767.electronic_wardrobe.OkHttpUtil;
import com.example.a10767.electronic_wardrobe.R;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.a10767.electronic_wardrobe.StaticVariable.login_account_Ed;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;

/**
 * 搭配适配器
 */
public class Make_UpAdapter extends BaseAdapter {
    private List<Make_Up> makeUpList;
    private LayoutInflater layoutInflater;
    private static final String TAG = "Make_UpAdapter";

    private String string;
    private Context context;
    private final int UPLOAD_SUCCESS = 1;
    private final int UPLOAD_FAIL = -1;
    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPLOAD_FAIL:
                    Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
                    break;
                case UPLOAD_SUCCESS:
                    Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public Make_UpAdapter(Context context, List<Make_Up> list) {
        makeUpList = list;
        this.context=context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return makeUpList.size();
    }

    @Override
    public Object getItem(int i) {
        return makeUpList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.content_listview, null);
            viewHolder.content_listView_label = view.findViewById(R.id.content_listView_label);
            viewHolder.content_listView_Img = view.findViewById(R.id.content_listView_Img);
            viewHolder.content_listview_heart = view.findViewById(R.id.content_listView_heart);
            view.setTag(viewHolder);//将ViewHolder存储在View中
        } else {
            viewHolder = (ViewHolder) view.getTag();//重新获取ViewHolder
        }
        viewHolder.content_listView_label.setText(makeUpList.get(i).getMixLabel());//获取便签并更新
        viewHolder.content_listView_Img.setImageResource(R.drawable.loading_jacket);//默认图片
        if (makeUpList.get(i).getmixCollect()) {
            viewHolder.content_listview_heart.setImageResource(R.drawable.heart_red);
        } else {
            viewHolder.content_listview_heart.setImageResource(R.drawable.heart_black);
        }


        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.content_listview_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = makeUpList.get(i).getmixCollect();
                String url = makeUpList.get(i).getMixPicture();
                String text = makeUpList.get(i).getMixLabel();
                String coolectid = String.valueOf(makeUpList.get(i).getMixId());
                makeUpList.get(i).setMixCollect(!flag);
                if (makeUpList.get(i).getmixCollect()) {
                    finalViewHolder.content_listview_heart.setImageResource(R.drawable.heart_red);
                    AtimationTools.scale(finalViewHolder.content_listview_heart);
                    uploadMessage(coolectid, url, text);
                } else {
                    finalViewHolder.content_listview_heart.setImageResource(R.drawable.heart_black);
                    AtimationTools.scale(finalViewHolder.content_listview_heart);
                    deleteMessage(coolectid, url, text);
                }
            }
        });

        String url = makeUpList.get(i).getMixPicture();//获取图片地址
        viewHolder.content_listView_Img.setTag(url);
        new ImageLoader().showImageByThread(viewHolder.content_listView_Img, url);//传递图片地址

        return view;
    }

    /**
     * 取消收藏
     *
     * @param coolectid
     * @param url2
     * @param text
     */
    private void deleteMessage(String coolectid, String url2, String text) {
        OkHttpUtil.postJson_othersCollect(url + "LoginTest2/deleteCollect3.action", coolectid, login_account_Ed, url2, text, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(UPLOAD_FAIL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                string = response.body().string();
                if (string.equals("success")) {
                    string = "取消收藏";
                }
                if (string.equals("fail")) {
                    string = "取消收藏";
                }
                handler.sendEmptyMessage(UPLOAD_SUCCESS);
            }
        });
    }

    /**
     * 收藏
     *
     * @param coolectid
     * @param url2
     * @param text
     */
    private void uploadMessage(String coolectid, String url2, String text) {
        OkHttpUtil.postJson_othersCollect(url + "LoginTest2/addCollect3.action", coolectid, login_account_Ed, url2, text, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(UPLOAD_FAIL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                string = response.body().string();
                if (string.equals("success")) {
                    string = "收藏成功-请在我的收藏中查看";
                }
                if (string.equals("fail")) {
                    string = "取消收藏";
                }
                handler.sendEmptyMessage(UPLOAD_SUCCESS);
            }
        });
    }

    class ViewHolder {
        ImageView content_listView_Img; //衣物图片
        TextView content_listView_label;//衣物标签
        ImageView content_listview_heart; //收藏
    }

}
