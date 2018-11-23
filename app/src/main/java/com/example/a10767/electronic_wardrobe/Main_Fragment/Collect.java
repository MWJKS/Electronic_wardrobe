package com.example.a10767.electronic_wardrobe.Main_Fragment;

/**
 * Created by 10767 on 2018/9/9.
 */

public class Collect {
    private String picture;
    private String txt;
    private String id;
    private boolean collect;
    private String heart_txt;

    public Collect(String picture, String txt, boolean collect, String heart_txt) {
        super();
        this.picture = picture;
        this.txt = txt;
        this.collect = collect;
        this.heart_txt = heart_txt;
    }

    public Collect() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public boolean isCollect() {
        return collect;
    }

    public void setCollect(boolean collect) {
        this.collect = collect;
    }

    public String getHeart_txt() {
        return heart_txt;
    }

    public void setHeart_txt(String heart_txt) {
        this.heart_txt = heart_txt;
    }
}
