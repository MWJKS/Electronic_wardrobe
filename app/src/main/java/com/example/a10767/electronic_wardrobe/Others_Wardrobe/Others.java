package com.example.a10767.electronic_wardrobe.Others_Wardrobe;

/**
 * Created by 10767 on 2018/8/13.
 */

public class Others {
    private String picture; //图片
    private String name;  //账号名称
    private String time;  //时间
    private String headPicture;  //头像
    private String contentText;  //便签
    private String number; //序号
    private String colour; //颜色
    private String style; //风格
    private String weather; //天气
    private String season; //季节
    private boolean collectId; //收藏标记

    public Others() {
        super();
    }

    public Others(String picture, String name, String time, String headPicture, String contentText, String number, String colour, String style, String weather, String season, boolean collectId) {
        super();
        this.contentText = contentText;
        this.headPicture = headPicture;
        this.time = time;
        this.picture = picture;
        this.name = name;
        this.style = style;
        this.colour = colour;
        this.number = number;
        this.season = season;
        this.weather = weather;
        this.collectId = collectId;
    }

    public String getSeason() {
        return season;
    }

    public boolean getCollectId() {
        return collectId;
    }

    public void setCollectId(boolean collectId) {
        this.collectId = collectId;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWeather() {
        return weather;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getHeadPicture() {
        return headPicture;
    }

    public void setHeadPicture(String headPicture) {
        this.headPicture = headPicture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPicture() {
        return picture;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
