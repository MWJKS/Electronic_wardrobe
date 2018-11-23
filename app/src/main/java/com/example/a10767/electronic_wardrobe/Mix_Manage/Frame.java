package com.example.a10767.electronic_wardrobe.Mix_Manage;

import android.graphics.Bitmap;

/**
 * Created by 10767 on 2018/8/3.
 */

/**
 * 框架
 */
public class Frame {
    private int MixID;
    private Bitmap frame_bitmap;
    private boolean selected;

    public Frame(int MixID, Bitmap frame_bitmap) {
        super();
        this.MixID = MixID;
        this.frame_bitmap = frame_bitmap;
    }

    public int getMixID() {
        return MixID;
    }

    public void setMixID(int MixID) {
        this.MixID = MixID;
    }

    public Bitmap getFrame_bitmap() {
        return frame_bitmap;
    }

    public void setBitmap(Bitmap frame_bitmap) {
        this.frame_bitmap = frame_bitmap;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
