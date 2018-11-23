package com.example.a10767.electronic_wardrobe.Clothes_Message;

/**
 * Created by 10767 on 2018/7/19.
 */


/**
 * 衣服声明类
 */
public class Clothes {
    private int cloId; //服装id
    private String clo_label; // 标签
    private boolean clo_collect; //收藏标识
    private String clo_color; // 衣物颜色
    private String style; // 衣物风格
    private String season; // 衣物季节
    private String weather; // 天气
    private String picture; // 图片
    private boolean mSelected; //判断复选框

    public Clothes() {
        super();
    }


    public Clothes(int cloId, String clo_label, String clo_color, String style, String season,
                   String weather, String picture, boolean selected) {
        super();
        this.cloId = cloId;
        this.clo_label = clo_label;
        this.clo_color = clo_color;
        this.style = style;
        this.season = season;
        this.weather = weather;
        this.picture = picture;
        mSelected = selected;

    }

    public int getCloId() {
        return cloId;
    }

    public void setCloId(int CloId) {
        this.cloId = CloId;
    }

    public String getClo_label() {
        return clo_label;
    }

    public void setClo_label(String clo_label) {
        this.clo_label = clo_label;
    }

    public String getClo_color() {
        return clo_color;
    }

    public void setClo_color(String clo_color) {
        this.clo_color = clo_color;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public boolean isClo_collect() {
        return clo_collect;
    }

    public void setClo_collect(boolean clo_collect) {
        this.clo_collect = clo_collect;
    }

}



