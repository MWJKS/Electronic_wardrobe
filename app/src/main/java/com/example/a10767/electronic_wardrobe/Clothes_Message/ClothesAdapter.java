package com.example.a10767.electronic_wardrobe.Clothes_Message;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.AtimationTools;
import com.example.a10767.electronic_wardrobe.OkHttpUtil;
import com.example.a10767.electronic_wardrobe.R;
import com.example.a10767.electronic_wardrobe.StaticVariable;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.a10767.electronic_wardrobe.StaticVariable.login_account_Ed;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;


/**
 * Created by 10767 on 2018/7/19.
 */


/**
 * 衣物适配器
 */
public class ClothesAdapter extends BaseAdapter {
    /*==============================================*/
    private List<Clothes> clothesList;
    private LayoutInflater mInflater;
    private String string;
    private static final String TAG = "ClothesAdapter";
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
/*后期更改*/
                    Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public ClothesAdapter(Context context, List<Clothes> objects) {
        clothesList = objects;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return clothesList.size();
    }

    @Override
    public Object getItem(int i) {
        return clothesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewholder = null;
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.content_listview, null);
            viewholder.content_listView_label = convertView.findViewById(R.id.content_listView_label);
            viewholder.content_listView_Img = convertView.findViewById(R.id.content_listView_Img);
            viewholder.content_listview_heart = convertView.findViewById(R.id.content_listView_heart);
            convertView.setTag(viewholder);//将ViewHolder存储在View中
        } else {
            viewholder = (ViewHolder) convertView.getTag();//重新获取ViewHolder
        }
        viewholder.content_listView_label.setText(clothesList.get(position).getClo_label());//获取便签并更新
        viewholder.content_listView_Img.setImageResource(R.drawable.loading_jacket);//默认图片
        if (clothesList.get(position).isClo_collect()) {
            viewholder.content_listview_heart.setImageResource(R.drawable.heart_red);
        } else {
            viewholder.content_listview_heart.setImageResource(R.drawable.heart_black);
        }

        final ViewHolder finalViewholder = viewholder;
        viewholder.content_listview_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = clothesList.get(position).isClo_collect();
                String url = clothesList.get(position).getPicture();
                String text = clothesList.get(position).getClo_label();
                String coolectid = String.valueOf(clothesList.get(position).getCloId());
                clothesList.get(position).setClo_collect(!flag);
                if (clothesList.get(position).isClo_collect()) {
                    finalViewholder.content_listview_heart.setImageResource(R.drawable.heart_red);
                    AtimationTools.scale(finalViewholder.content_listview_heart);
                    uploadMessage(coolectid, url, text);
                } else {
                    finalViewholder.content_listview_heart.setImageResource(R.drawable.heart_black);
                    AtimationTools.scale(finalViewholder.content_listview_heart);
                    deleteMessage(coolectid, url, text);
                }
            }
        });


        String url = clothesList.get(position).getPicture();//获取图片地址
        viewholder.content_listView_Img.setTag(url);
        new ImageLoader().showImageByThread(viewholder.content_listView_Img, url);//传递图片地址

        return convertView;
    }

    /**
     * 取消收藏
     *
     * @param coolectid
     * @param url2
     * @param text
     */
    private void deleteMessage(String coolectid, String url2, String text) {
        OkHttpUtil.postJson_othersCollect(url + "LoginTest2/deleteCollect2.action", coolectid, login_account_Ed, url2, text, new Callback() {
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
     * 收藏数据
     * @param coolectid
     * @param url2
     * @param text
     */
    private void uploadMessage(final String coolectid, final String url2, final String text) {
        OkHttpUtil.postJson_othersCollect(url + "LoginTest2/addCollect2.action", coolectid, login_account_Ed, url2, text, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(UPLOAD_FAIL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                string = response.body().string();
                Log.d(TAG,url+"LoginTest2/addCollectP.action"+"序号:"+coolectid+"账号："+login_account_Ed+"图片地址："+url2+"文本："+text);
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