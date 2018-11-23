package com.example.a10767.electronic_wardrobe;

import android.app.DownloadManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.stream.Stream;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.ByteString;

/**
 * Created by 10767 on 2018/6/22.
 */

public class OkHttpUtil {

    public static void postJson_MixSearchClothes(String url, String username,String search_S,okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("search_S",search_S)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }





    /**
     * 收藏
     * @param url
     * @param username
     * @param othersurl
     * @param otherstext
     * @param callback
     */
    public static void postJson_othersCollect(String url, String collectid,String username,String othersurl,String otherstext,okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("collectid", collectid)
                .add("username", username)
                .add("othersurl",othersurl)
                .add("othertext",otherstext)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }


    /**
     * 显示衣物
     * @param url       地址
     * @param username  账号
     * @param clo_name  类型
     * @param callback
     */
    public static void postJson_SearchClothes(String url, String username,String clo_name,String search_S,okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("search_S",search_S)
                .add("clo_name",clo_name)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }


    /**
     *  分享访问地址
     * @param url
     * @param callback
     */
    public static  void postJson_OthersClothes(String url,String username,okhttp3.Callback callback)
    {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }



    /**
     * 删除模块
     * @param url
     * @param username
     * @param cloId 传递的衣物序号
     * @param callback
     */
    public static void postJson_DeleteClothes(String url, String username,String cloId, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("cloId",cloId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }



    /**
     * 显示衣物
     * @param url       地址
     * @param username  账号
     * @param clo_name  类型
     * @param callback
     */
    public static void postJson_showClothes(String url, String username,String clo_name, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("clo_name",clo_name)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 传递账号用于显示个人信息
     *
     * @param url
     * @param username
     * @param callback
     */
    public static void postJson_showMessage(String url, String username, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }


    /**
     * 个人信息中电话号码修改
     *
     * @param url
     * @param username
     * @param phoneNumber
     * @param callback
     */
    public static void okhttputil_fix_phoneNumber(String url, String username, String phoneNumber, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("phone", phoneNumber)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 个人信息中修改密码
     *
     * @param url
     * @param username
     * @param new_password
     * @param callback
     */
    public static void okhttputil_fix_Password(String url, String username, String new_password, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("password", new_password)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 个人信息中性别修改
     *
     * @param url
     * @param username
     * @param gender
     * @param callback
     */
    public static void send_gender(String url, String username, String gender, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("grade", gender)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 发送用户名称
     *
     * @param url
     * @param username
     * @param useralias
     * @param callback
     */
    public static void send_username(String url, String username, String useralias, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("useralias", useralias)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 发送注册信息
     *
     * @param url
     * @param account
     * @param password
     * @param phone
     * @param callback
     */
    public static void okhttputil_register(String url, String account, String password, String phone, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("username", account)
                .add("password", password)
                .add("phone", phone)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 获取验证码
     *
     * @param url
     * @param phone
     * @param callback
     */
    public static void okhttputil_validate(String url, String phone, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("phone", phone)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 发送登陆信息
     *
     * @param url
     * @param account
     * @param password
     * @param callback
     */
    public static void okhttputil_login(String url, String account, String password, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("username", account)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 提交新密码
     *
     * @param url
     * @param new_password
     * @param callback
     */
    public static void okhttputil_change_Password(String url, String phone, String new_password, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("password", new_password)
                .add("phone", phone)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }


}
