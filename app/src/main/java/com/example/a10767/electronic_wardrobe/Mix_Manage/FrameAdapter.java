package com.example.a10767.electronic_wardrobe.Mix_Manage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.example.a10767.electronic_wardrobe.R;

import java.util.List;

/**
 * Created by 10767 on 2018/8/5.
 */

public class FrameAdapter extends BaseAdapter {
    private List<Mix> frameList;
    private LayoutInflater mInflater;

    public FrameAdapter(Context context, List<Mix> objects) {
        frameList = objects;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return frameList.size();
    }

    @Override
    public Object getItem(int i) {
        return frameList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Mix mix = (Mix) getItem(i);
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.content_frame_listview, null);
            viewHolder.content_frame_IMG = view.findViewById(R.id.content_frame_IMG);
            view.setTag(viewHolder);//将ViewHolder存储在View中
        } else {
            viewHolder = (ViewHolder) view.getTag();//重新获取ViewHolder
        }
        viewHolder.content_frame_IMG.setImageBitmap(frameList.get(i).getBitmap());
        return view;
    }

    class ViewHolder {
        ImageView content_frame_IMG; //衣物图片
    }
}
