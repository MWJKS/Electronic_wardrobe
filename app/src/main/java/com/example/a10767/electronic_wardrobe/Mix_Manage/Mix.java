package com.example.a10767.electronic_wardrobe.Mix_Manage;

/**
 * Created by 10767 on 2018/8/5.
 */

import android.graphics.Bitmap;

/**
 * 衣物选择的属性
 */

public class Mix {
    private Bitmap bitmap;
    private String bitmap_url;

    public Mix(Bitmap bitmap) {
        super();
        this.bitmap = bitmap;
    }

    public String getBitmap_url() {
        return bitmap_url;
    }

    public void setBitmap_url(String bitmap_url) {
        this.bitmap_url = bitmap_url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}
