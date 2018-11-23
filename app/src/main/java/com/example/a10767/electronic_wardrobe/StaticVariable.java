package com.example.a10767.electronic_wardrobe;


import android.graphics.Bitmap;

import com.example.a10767.electronic_wardrobe.Mix_Manage.Mix;

import java.util.ArrayList;

/**
 * Created by 10767 on 2018/7/31.
 */

public class StaticVariable {
//        public static final String url = "http://192.168.0.101:8080/"; //地址
    public static final String url = "http://47.106.111.236:8080/"; //地址
    public static int themain = 1; //碎片位置
    public static String clo_name; //衣服种类
    public static int clo_id; //衣服id
    public static String login_account_Ed; //账号
    public static boolean isExit; //退出boolean
    public static String search_content; //查询内容
    public static String checkOrSearch; //查询还是检查
    public static int frame_number; //框架序号
    public static ArrayList<Mix> mixFrameList; //搭配的集合
    public static int mixPosition; //搭配点击位置
    public static Bitmap mainBitmap; //搭配最终图
    public static Bitmap othersBitmap; //添加时的图片
    public static String mix_choice; //选择搭配返回时的标识符

}
