package com.example.a10767.electronic_wardrobe.Mix_Manage;

/**
 * Created by 10767 on 2018/8/9.
 */


/**
 * 搭配添加类
 */
public class Make_Up {

    private int mixId;
    private String mixWeather;
    private String mixStyle;
    private String mixPicture;
    private String mixOccasion;
    private String mixSeason;
    private String mixLabel;
    private boolean mixCollect;

    public Make_Up(int mixId, String mixWeather, String mixStyle, String mixPicture, String mixOccasion, String mixSeason,
                   String mixLabel) {
        super();
        this.mixId = mixId;
        this.mixWeather = mixWeather;
        this.mixStyle = mixStyle;
        this.mixPicture = mixPicture;
        this.mixOccasion = mixOccasion;
        this.mixSeason = mixSeason;
        this.mixLabel = mixLabel;
    }

    public Make_Up() {
        super();
        // TODO Auto-generated constructor stub
    }

    public boolean getmixCollect() {
        return mixCollect;
    }

    public void setMixCollect(boolean mixCollect) {
        this.mixCollect = mixCollect;
    }


    public int getMixId() {
        return mixId;
    }

    public void setMixId(int mixId) {
        this.mixId = mixId;
    }

    public String getMixWeather() {
        return mixWeather;
    }

    public void setMixWeather(String mixWeather) {
        this.mixWeather = mixWeather;
    }

    public String getMixStyle() {
        return mixStyle;
    }

    public void setMixStyle(String mixStyle) {
        this.mixStyle = mixStyle;
    }

    public String getMixPicture() {
        return mixPicture;
    }

    public void setMixPicture(String mixPicture) {
        this.mixPicture = mixPicture;
    }

    public String getMixOccasion() {
        return mixOccasion;
    }

    public void setMixOccasion(String mixOccasion) {
        this.mixOccasion = mixOccasion;
    }

    public String getMixSeason() {
        return mixSeason;
    }

    public void setMixSeason(String mixSeason) {
        this.mixSeason = mixSeason;
    }

    public String getMixLabel() {
        return mixLabel;
    }

    public void setMixLabel(String mixLabel) {
        this.mixLabel = mixLabel;
    }


}
