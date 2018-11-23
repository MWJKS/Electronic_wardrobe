package com.example.a10767.electronic_wardrobe.Others_Wardrobe;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.AtimationTools;
import com.example.a10767.electronic_wardrobe.Clothes_Message.ImageLoader;
import com.example.a10767.electronic_wardrobe.OkHttpUtil;
import com.example.a10767.electronic_wardrobe.R;

import java.io.IOException;
import java.util.List;
import java.util.logging.Handler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.a10767.electronic_wardrobe.StaticVariable.login_account_Ed;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;

/**
 * Created by 10767 on 2018/8/13.
 */

public class OthersAdapter extends BaseAdapter {
    private List<Others> othersList;
    private Context context;
    private LayoutInflater layoutInflater;
    private static final String TAG = "OthersAdapter";
    private String url1;
    private String url2;
    private String string;
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

    public OthersAdapter(Context context, List<Others> object) {
        othersList = object;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return othersList.size();
    }

    @Override
    public Object getItem(int i) {
        return othersList.get(i);
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
            view = layoutInflater.inflate(R.layout.content_others_listview, null);
            viewHolder.others_wardrobe_content = view.findViewById(R.id.others_wardrobe_content);
            viewHolder.others_wardrobe_IMG = view.findViewById(R.id.others_wardrobe_IMG);
            viewHolder.others_wardrobe_time = view.findViewById(R.id.others_wardrobe_time);
            viewHolder.others_wardrobe_picture = view.findViewById(R.id.others_wardrobe_picture);
            viewHolder.others_wardrobe_name = view.findViewById(R.id.others_wardrobe_name);
            viewHolder.others_wardrobe_collect = view.findViewById(R.id.others_wardrobe_collect);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();//重新获取ViewHolder
        }
        viewHolder.others_wardrobe_name.setText(othersList.get(i).getName());
        viewHolder.others_wardrobe_content.setText(othersList.get(i).getContentText());
        viewHolder.others_wardrobe_time.setText(othersList.get(i).getTime());
        viewHolder.others_wardrobe_picture.setImageResource(R.drawable.empty); //默认图片
        viewHolder.others_wardrobe_IMG.setImageResource(R.drawable.head);

        if (othersList.get(i).getCollectId()) {
            viewHolder.others_wardrobe_collect.setImageResource(R.drawable.heart_red);
        } else {
            viewHolder.others_wardrobe_collect.setImageResource(R.drawable.heart_black);
        }


        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.others_wardrobe_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = othersList.get(i).getCollectId();
                String url = othersList.get(i).getPicture();
                String text = othersList.get(i).getContentText();
                String coolectid = othersList.get(i).getNumber();
                othersList.get(i).setCollectId(!flag);
                if (othersList.get(i).getCollectId()) {
                    finalViewHolder.others_wardrobe_collect.setImageResource(R.drawable.heart_red);
                    AtimationTools.scale(finalViewHolder.others_wardrobe_collect);
                    uploadMessage(coolectid, url, text);
                } else {
                    finalViewHolder.others_wardrobe_collect.setImageResource(R.drawable.heart_black);
                    AtimationTools.scale(finalViewHolder.others_wardrobe_collect);
                    deleteMessage(coolectid, url, text);
                }
            }
        });

        /*=======================*/
        url1 = othersList.get(i).getPicture();
        url2 = othersList.get(i).getHeadPicture();
        if (url1 != "") {
            new ImageLoader().showImageByThread(viewHolder.others_wardrobe_picture, url1);//传递图片地址
        }
        new ImageLoader().showImageByThread(viewHolder.others_wardrobe_IMG, url2);//传递头像图片地址
        viewHolder.others_wardrobe_IMG.setTag(url2);
        viewHolder.others_wardrobe_picture.setTag(url1);
        return view;
    }

    /**
     * 上传信息
     *
     * @param collectid
     * @param pictureurl
     * @param text
     */
    private void uploadMessage(String collectid, String pictureurl, String text) {
        OkHttpUtil.postJson_othersCollect(url + "LoginTest2/addCollect1.action", collectid, login_account_Ed, pictureurl, text, new Callback() {
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

    /**
     * 删除信息
     * @param collectid
     * @param pictureurl
     * @param text
     */
    private void deleteMessage(String collectid, String pictureurl, String text) {
        OkHttpUtil.postJson_othersCollect(url + "LoginTest2/deleteCollect1.action", collectid, login_account_Ed, pictureurl, text, new Callback() {
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


    class ViewHolder {
        ImageView others_wardrobe_IMG;
        ImageView others_wardrobe_picture;
        TextView others_wardrobe_name;
        TextView others_wardrobe_time;
        TextView others_wardrobe_content;
        ImageView others_wardrobe_collect;
    }

}
