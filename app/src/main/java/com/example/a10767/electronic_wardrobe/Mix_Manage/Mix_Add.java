package com.example.a10767.electronic_wardrobe.Mix_Manage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.Main_Fragment.MyselfMine;
import com.example.a10767.electronic_wardrobe.Main_Fragment.TheMain;
import com.example.a10767.electronic_wardrobe.R;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static com.example.a10767.electronic_wardrobe.StaticVariable.login_account_Ed;
import static com.example.a10767.electronic_wardrobe.StaticVariable.mainBitmap;
import static com.example.a10767.electronic_wardrobe.StaticVariable.themain;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;

/**
 * Created by 10767 on 2018/8/8.
 */

public class Mix_Add extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Mix_Add";
    /*UI*/
    private LinearLayout mix_add_return; //退出
    private ImageView mix_add_IMG; //图片
    private Button mix_add_save; //保存
    private TextView mix_add_occasion;
    private TextView mix_add_style;
    private TextView mix_add_season;
    private TextView mix_add_weather;
    private TextView mix_add_text;
    /* 用于保存编辑框中的字符串 */
    private String mix_occasion_S = "";
    private String mix_style_S = "";
    private String mix_season_S = "";
    private String mix_weather_S = "";
    private String mix_text_S = "";
    /*上传*/
    private File outputImage;
    private Map<String, String> map = new HashMap<String, String>();
    private ProgressDialog progressDialog;
    private static final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
    private static final String PREFIX = "--";
    private static final String LINE_END = "\r\n";
    private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
    private static final String CHARSET = "utf-8"; // 设置编码
    private int readTimeOut = 10 * 1000; // 读取超时
    private int connectTimeout = 10 * 1000; // 超时时间
    /***
     * 请求使用多长时间
     */
    private static int requestTime = 0;
    /* Handler变量 */
    private final int UPLOAD_SUCCESS = 1;
    private final int UPLOAD_FAIL = -1;
    private final int FILE_ERROR = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPLOAD_SUCCESS:
                    progressDialog.dismiss();
                    Toast.makeText(Mix_Add.this, "保存成功，请在我的搭配中查看", Toast.LENGTH_SHORT).show();
                    themain = 3;
                    startActivity(new Intent(Mix_Add.this, TheMain.class));
                    overridePendingTransition(R.anim.enter2, R.anim.quit2);
                    break;
                case UPLOAD_FAIL:
                    Toast.makeText(Mix_Add.this, "网络连接错误,上传失败", Toast.LENGTH_SHORT).show();
                    break;
                case FILE_ERROR:
                    Toast.makeText(Mix_Add.this, "请选择图片", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mix_add);
        initUI();
        initView();
        ActivityCollector.addActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Mix_Add.this, Frame_All.class));
        overridePendingTransition(R.anim.enter2, R.anim.quit2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initView() {
        mix_add_IMG.setImageBitmap(mainBitmap); //加载图片
        mix_add_return.setOnClickListener(this);
        mix_add_save.setOnClickListener(this);
        mix_add_IMG.setOnClickListener(this);
    }

    private void initUI() {
        progressDialog = new ProgressDialog(Mix_Add.this);
        progressDialog.setMessage("加载中....");
        progressDialog.setCancelable(false);
        mix_add_return = findViewById(R.id.mix_add_return);
        mix_add_save = findViewById(R.id.mix_add_save);
        mix_add_IMG = findViewById(R.id.mix_add_IMG);
        mix_add_occasion = findViewById(R.id.mix_add_occasion);
        mix_add_style = findViewById(R.id.mix_add_style);
        mix_add_season = findViewById(R.id.mix_add_season);
        mix_add_weather = findViewById(R.id.mix_add_weather);
        mix_add_text = findViewById(R.id.mix_add_text);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mix_add_return:
                startActivity(new Intent(Mix_Add.this, Frame_All.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.mix_add_save:
                progressDialog.show();
                receiveMessage();//接受数据
                break;
            case R.id.mix_add_IMG:  //编写放大情景
                break;
        }
    }

    /**
     * 接受数据
     */
    private void receiveMessage() {
        saveBitmapFile(mainBitmap); //将Bitmap转化为文件
        mix_occasion_S = mix_add_occasion.getText().toString();
        mix_season_S = mix_add_season.getText().toString();
        mix_style_S = mix_add_style.getText().toString();
        mix_text_S = mix_add_text.getText().toString();
        mix_weather_S = mix_add_weather.getText().toString();
        map.put("username", login_account_Ed);
        map.put("mixOccasion", mix_occasion_S);
        map.put("mixSeason", mix_season_S);
        map.put("mixStyle", mix_style_S);
        map.put("mixWeather", mix_weather_S);
        map.put("mixLabel", mix_text_S);
        Log.d(TAG, "用户名：" + login_account_Ed
                + "颜色:" + mix_occasion_S
                + "季节:" + mix_season_S
                + "风格:" + mix_style_S
                + "天气:" + mix_weather_S
                + "便签:" + mix_text_S
                + "图片" + outputImage);
        uploadClothes(outputImage, url + "LoginTest2/insertComboClothes.action", map); //上传方法
    }

    /**
     * bitmap对象转化为file文件
     *
     * @param bitmap
     */
    public void saveBitmapFile(Bitmap bitmap) {
        outputImage = new File("/mnt/sdcard/01.jpg");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputImage));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("broken");
        }
    }

    /**
     * 上传参数
     *
     * @param file
     * @param RequestURL
     * @param map
     */
    private void uploadClothes(final File file, final String RequestURL, final Map<String, String> map) {
        if (file == null || (!file.exists())) {
            Log.d(TAG, "文件不存在");
            handler.sendEmptyMessage(FILE_ERROR);
            return;
        }
        Log.i(TAG, "请求的URL=" + RequestURL);
        Log.i(TAG, "请求的fileName=" + file.getName());
        new Thread(new Runnable() { //开启线程上传文件
            @Override
            public void run() {
                toUploadFile(file, RequestURL, map);
            }
        }).start();
    }

    /**
     * 上传图片
     *
     * @param file
     * @param RequestURL
     * @param param
     */
    private void toUploadFile(File file, String RequestURL, Map<String, String> param) {
        String result = null;
        requestTime = 0;
        long requestTime = System.currentTimeMillis();
        long responseTime = 0;
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(readTimeOut);
            conn.setConnectTimeout(connectTimeout);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

            /**
             * 当文件不为空，把文件包装并且上传
             */
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            StringBuffer sb = null;
            String params = "";

            /***
             * 以下是用于上传参数
             */
            if (param != null && param.size() > 0) {
                Iterator<String> it = param.keySet().iterator();
                while (it.hasNext()) {
                    sb = null;
                    sb = new StringBuffer();
                    String key = it.next();
                    String value = param.get(key);
                    sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                    sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);
                    sb.append(value).append(LINE_END);
                    params = sb.toString();
                    Log.i(TAG, key + "=" + params + "##");
                    dos.write(params.getBytes());
                    // dos.flush();
                }
            }
            sb = null;
            params = null;
            sb = new StringBuffer();
            /**
             * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的 比如:abc.png
             */
            sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
            sb.append("Content-Disposition:form-data; name=\"" + "mixPicture"
                    + "\"; filename=\"" + file.getName() + "\"" + LINE_END);
            sb.append("Content-Type:image/pjpeg" + LINE_END); // 这里配置的Content-type很重要的 ，用于服务器端辨别文件的类型的
            sb.append(LINE_END);
            params = sb.toString();
            sb = null;
            Log.i(TAG, file.getName() + "=" + params + "##");
            dos.write(params.getBytes());
                         /*上传文件*/
            InputStream is = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int len = 0;
            int curLen = 0;
            while ((len = is.read(bytes)) != -1) {
                curLen += len;
                dos.write(bytes, 0, len);
            }
            is.close();

            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
            dos.write(end_data);
            dos.flush();
            /**
             * 获取响应码 200=成功 当响应成功，获取响应的流
             */
            int res = conn.getResponseCode();
            responseTime = System.currentTimeMillis();
            this.requestTime = (int) ((responseTime - requestTime) / 1000);
            Log.e(TAG, "response code:" + res);
            if (res == 200) {
                Log.e(TAG, "request success");
                handler.sendEmptyMessage(UPLOAD_SUCCESS);
                return;
            } else {
                handler.sendEmptyMessage(UPLOAD_FAIL);
                Log.e(TAG, "request error" + res);
                Log.d(TAG, "上传失败：code=" + res);
                return;
            }
        } catch (MalformedURLException e) {
            handler.sendEmptyMessage(UPLOAD_FAIL);
            Log.d(TAG, "上传失败：error=" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (IOException e) {
            handler.sendEmptyMessage(UPLOAD_FAIL);
            Log.d(TAG, "上传失败：error=" + e.getMessage());
            e.printStackTrace();
            return;
        }
    }
}
