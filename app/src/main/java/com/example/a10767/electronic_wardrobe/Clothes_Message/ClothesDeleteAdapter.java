package com.example.a10767.electronic_wardrobe.Clothes_Message;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a10767.electronic_wardrobe.R;

import java.util.List;

/**
 * Created by 10767 on 2018/7/26.
 */

public class ClothesDeleteAdapter extends BaseAdapter {
    private List<Clothes> delete_clothesList;
    private Clothes clothes;
    private LayoutInflater delete_mInflater;
    private Context context;
    private ViewHolder viewHolder = null;


    private boolean[] checks;//定义数组来保存CheckBox的状态

    public ClothesDeleteAdapter(Context context, List<Clothes> objects) {
        delete_clothesList = objects;
        this.context = context;
        checks = new boolean[objects.size()];//初始化数组
        delete_mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return delete_clothesList.size();
    }

    @Override
    public Object getItem(int i) {
        return delete_clothesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int position = i;//定义一个final的int类型pos用来记录点击的位置
        if (view == null) {
            viewHolder = new ViewHolder();
            view = delete_mInflater.inflate(R.layout.content_delete_listview, null);
            viewHolder.content_delete_listView_Img = view.findViewById(R.id.content_delete_listView_Img);
            viewHolder.content_delete_listView_label = view.findViewById(R.id.content_delete_listView_label);
            viewHolder.clothes_delete_CB = view.findViewById(R.id.clothes_delete_CB);
            view.setTag(viewHolder);//将ViewHolder存储在View中
        } else {
            viewHolder = (ViewHolder) view.getTag();//重新获取ViewHolder
        }
        viewHolder.content_delete_listView_Img.setImageResource(R.drawable.loading_jacket);//默认图片
        String url = delete_clothesList.get(i).getPicture();
        viewHolder.content_delete_listView_Img.setTag(url);
        new ImageLoader().showImageByThread(viewHolder.content_delete_listView_Img, url);//传递图片地址
        viewHolder.content_delete_listView_label.setText(delete_clothesList.get(position).getClo_label());//获取便签并更新
        /*=======================================复选框点击事件==========================================*/
        viewHolder.clothes_delete_CB.setChecked(delete_clothesList.get(position).isSelected());//设置选中与否
        viewHolder.clothes_delete_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
                delete_clothesList.get(position).setSelected(check);//设置这个位置的是否选中情况
            }
        });
        return view;
    }

    class ViewHolder {
        ImageView content_delete_listView_Img; //衣物图片
        TextView content_delete_listView_label;//衣物标签
        CheckBox clothes_delete_CB; //删除选框
    }

}
