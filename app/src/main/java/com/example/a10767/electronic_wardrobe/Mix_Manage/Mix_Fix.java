package com.example.a10767.electronic_wardrobe.Mix_Manage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.Clothes_Manage.Clothes_Check;
import com.example.a10767.electronic_wardrobe.Clothes_Manage.Clothes_Fix;
import com.example.a10767.electronic_wardrobe.OkHttpUtil;
import com.example.a10767.electronic_wardrobe.R;

import org.json.JSONException;
import org.json.JSONObject;

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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.a10767.electronic_wardrobe.StaticVariable.clo_id;
import static com.example.a10767.electronic_wardrobe.StaticVariable.clo_name;
import static com.example.a10767.electronic_wardrobe.StaticVariable.login_account_Ed;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;

public class Mix_Fix extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Mix_Fix";
    private ImageView mix_fix_IMG; //图片
    private Bitmap bitmap;
    private File outputImage;
    private EditText mix_fix_scene; //颜色
    private String mix_fix_scene_Ed = "";
    private EditText mix_fix_style; //风格
    private String mix_fix_style_Ed = "";
    private EditText mix_fix_season;
    private String mix_fix_season_Ed = "";
    private EditText mix_fix_weather;
    private String mix_fix_weather_Ed = "";
    private EditText mix_fix_text;
    private String mix_fix_text_Ed = "";
    private LinearLayout mix_fix_return; //返回
    private Button mix_fix_save; //修改
    private Map<String, String> map = new HashMap<String, String>();
    private ProgressDialog progressDialog; //等待

    /*==========================================================*/
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
    private final int SHOW_ERROR = -1;
    private final int UPLOAD_FAIL = -2;
    private final int SHOW_SUCCESS = 1;
    private final int UPLOAD_SUCCESS = 2;
    private final int FILE_ERROR = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_ERROR:
                    Toast.makeText(Mix_Fix.this, "网络错误，加载失败", Toast.LENGTH_SHORT).show();
                    break;
                case SHOW_SUCCESS:
                    mix_fix_scene.setText(mix_fix_scene_Ed);
                    mix_fix_style.setText(mix_fix_style_Ed);
                    mix_fix_season.setText(mix_fix_season_Ed);
                    mix_fix_weather.setText(mix_fix_weather_Ed);
                    mix_fix_text.setText(mix_fix_text_Ed);
                    mix_fix_IMG.setImageBitmap(bitmap);
                    progressDialog.dismiss();
                    break;
                case FILE_ERROR:
                    progressDialog.dismiss();
                    Toast.makeText(Mix_Fix.this, "请选择图片", Toast.LENGTH_SHORT).show();
                    break;
                case UPLOAD_SUCCESS:
                    progressDialog.dismiss();
                    Toast.makeText(Mix_Fix.this, "保存成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Mix_Fix.this, Mix_Check.class));
                    overridePendingTransition(R.anim.enter2, R.anim.quit2);
                    break;
                case UPLOAD_FAIL:
                    progressDialog.dismiss();
                    Toast.makeText(Mix_Fix.this, "网络连接错误,修改失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mix_fix);
        initUI();
        initView();
        ActivityCollector.addActivity(this);
        uploadMessage();//接受数据

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Mix_Fix.this, Mix_Check.class));
        overridePendingTransition(R.anim.enter2, R.anim.quit2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initView() {
        progressDialog.setCancelable(false);
        mix_fix_save.setOnClickListener(this);
        mix_fix_return.setOnClickListener(this);
    }

    private void initUI() {
        progressDialog = new ProgressDialog(Mix_Fix.this);
        progressDialog.setMessage("保存中....");
        mix_fix_IMG = findViewById(R.id.mix_fix_IMG);
        mix_fix_scene = findViewById(R.id.mix_fix_scene);
        mix_fix_style = findViewById(R.id.mix_fix_style);
        mix_fix_season = findViewById(R.id.mix_fix_season);
        mix_fix_weather = findViewById(R.id.mix_fix_weather);
        mix_fix_text = findViewById(R.id.mix_fix_text);
        mix_fix_return = findViewById(R.id.mix_fix_return);
        mix_fix_save = findViewById(R.id.mix_fix_save);
    }

    /**
     * 加载信息
     */
    private void uploadMessage() {
        progressDialog.show();
        OkHttpUtil.postJson_DeleteClothes(url + "LoginTest2/findComInformation.action", login_account_Ed, String.valueOf(clo_id), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(SHOW_ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String show_message = response.body().string();
                Log.d(TAG, show_message);
                try {
                    JSONObject jsonObject = new JSONObject(show_message);
                    mix_fix_scene_Ed = jsonObject.getString("clomatchoccasion");
                    mix_fix_style_Ed = jsonObject.getString("clomatchstyle");
                    mix_fix_season_Ed = jsonObject.getString("clomatchseason");
                    mix_fix_weather_Ed = jsonObject.getString("clomatchweather");
                    mix_fix_text_Ed = jsonObject.getString("clomatchlabel");
                    bitmap = getBitmap(jsonObject.getString("clomatchpicture"));
                    saveBitmapFile(bitmap);
                    handler.sendEmptyMessage(SHOW_SUCCESS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mix_fix_save:
                progressDialog.show();
                receiveMessage();//先接受数据
                break;
            case R.id.mix_fix_return:
                startActivity(new Intent(Mix_Fix.this, Mix_Check.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
        }
    }

    /**
     * 接受上传数据
     */
    private void receiveMessage() {
        mix_fix_scene_Ed = mix_fix_scene.getText().toString();
        mix_fix_season_Ed = mix_fix_season.getText().toString();
        mix_fix_style_Ed = mix_fix_style.getText().toString();
        mix_fix_weather_Ed = mix_fix_weather.getText().toString();
        mix_fix_text_Ed = mix_fix_text.getText().toString();
        map.put("username", login_account_Ed);
        map.put("mixId", String.valueOf(clo_id));
        map.put("mixScene", mix_fix_scene_Ed);
        map.put("mixSeason", mix_fix_season_Ed);
        map.put("mixStyle", mix_fix_style_Ed);
        map.put("mixWeather", mix_fix_weather_Ed);
        map.put("mixLabel", mix_fix_text_Ed);
        Log.d(TAG, "用户名：" + login_account_Ed
                + "CloId:" + clo_id
                + "场合: "+ mix_fix_scene_Ed
                + "季节:" + mix_fix_season_Ed
                + "风格:" + mix_fix_style_Ed
                + "天气:" + mix_fix_weather_Ed
                + "便签:" + mix_fix_text_Ed
                + "图片:" + outputImage);
        uploadClothes(outputImage, url + "LoginTest2/updateCombo.action", map); //上传方法
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
     * 获取图片
     *
     * @param path
     * @return
     * @throws IOException
     */
    private Bitmap getBitmap(String path) throws IOException {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }
        return null;
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
            sb.append("Content-Disposition:form-data; name=\"" + "cloPicture"
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
