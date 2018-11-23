package com.example.a10767.electronic_wardrobe.Mix_Manage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.a10767.electronic_wardrobe.R;

import java.util.List;

import static com.example.a10767.electronic_wardrobe.StaticVariable.frame_number;

/**
 * Created by 10767 on 2018/8/3.
 */

/**
 * 框架适配器
 */
public class MixAdapter extends RecyclerView.Adapter<MixAdapter.ViewHolder> {
    private List<Frame> frameList;

    class ViewHolder extends RecyclerView.ViewHolder {
        View mixView;
        ImageView content_mix_listView_IMG;
        ImageView content_mix_listView_choice;

        public ViewHolder(View view) {
            super(view);
            mixView = view;
            content_mix_listView_IMG = view.findViewById(R.id.content_mix_listView_IMG);
            content_mix_listView_choice = view.findViewById(R.id.content_mix_listView_choice);
            content_mix_listView_choice.setVisibility(View.GONE);
        }
    }

    public MixAdapter(List<Frame> frameList) {
        this.frameList = frameList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_mix_listview, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.mixView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Frame frame = frameList.get(position);
                frame_number = frame.getMixID();
                frame.setSelected(!frame.isSelected());
                if (frame.isSelected()) {
                    holder.content_mix_listView_choice.setVisibility(View.VISIBLE);
                   for (int i=0;i<3;i++)
                   {
                       if (i!=position)
                       {
                           frame = frameList.get(i);
                           frame.setSelected(false);
                           notifyDataSetChanged();
                       }
                   }
                } else {
                    holder.content_mix_listView_choice.setVisibility(View.GONE);
                }

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Frame frame = frameList.get(position);
        holder.content_mix_listView_IMG.setImageBitmap(frame.getFrame_bitmap());
        if (frame.isSelected()) {
            holder.content_mix_listView_choice.setVisibility(View.VISIBLE);
        } else {
            holder.content_mix_listView_choice.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return frameList.size();
    }


}
