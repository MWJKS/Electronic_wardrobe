package com.example.a10767.electronic_wardrobe.Clothes_Manage;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.Clothes_Message.Clothe_Accessories;
import com.example.a10767.electronic_wardrobe.R;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import static com.example.a10767.electronic_wardrobe.Main_Fragment.MyselfMine.CHOOSE_PHOTO;
import static com.example.a10767.electronic_wardrobe.Main_Fragment.MyselfMine.TAKE_PHOTO;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;

/**
 * 饰品添加类
 */

public class Accessories_Add extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Cap_Add";
    /*UI*/
    private Dialog dialog;
    private ViewGroup viewGroup;
    private LinearLayout clothes_add_return;  //返回
    private Button clothes_add_save; //保存
    private EditText clothes_add_color; //颜色
    private EditText clothes_add_style; //风格
    private EditText clothes_add_season;//季节
    private EditText clothes_add_weather;//天气
    private EditText clothes_add_text; //便签
    private TextView start_head_portrait;//拍照按钮
    private TextView start_photos;//图库按钮
    private TextView start_cancel;//取消按钮
    /*上传UI*/
    private Bitmap bitmap_imp; //初始化图片
    private File outputImage;
    private Uri imageUri;
    private ImageView clothes_add_IMG; //衣物图片
    private Map<String, String> map = new HashMap<String, String>();

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
    /* 用于保存编辑框中的字符串 */
    private String clothes_add_color_S = "";
    private String clothes_add_style_S = "";
    private String clothes_add_season_S = "";
    private String clothes_add_weather_S = "";
    private String clothes_add_text_S = "";
    private String clothes_type = "饰品";
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
                    Toast.makeText(Accessories_Add.this, "保存成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Accessories_Add.this, Clothe_Accessories.class));
                    overridePendingTransition(R.anim.enter2, R.anim.enter2);
                    break;
                case UPLOAD_FAIL:
                    Toast.makeText(Accessories_Add.this, "网络连接诶错误,上传失败", Toast.LENGTH_SHORT).show();
                    break;
                case FILE_ERROR:
                    Toast.makeText(Accessories_Add.this, "请选择图片", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clothes_add);
        initUI();
        initView();
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initUI() {
        clothes_add_return = findViewById(R.id.clothes_add_return);
        clothes_add_save = findViewById(R.id.clothes_add_save);
        clothes_add_color = findViewById(R.id.clothes_add_color);
        clothes_add_style = findViewById(R.id.clothes_add_style);
        clothes_add_season = findViewById(R.id.clothes_add_season);
        clothes_add_weather = findViewById(R.id.clothes_add_weather);
        clothes_add_text = findViewById(R.id.clothes_add_text);
        clothes_add_IMG = findViewById(R.id.clothes_add_IMG);
        viewGroup = (ViewGroup) LayoutInflater.from(Accessories_Add.this).inflate(R.layout.head_portrait_dialog, null);
        start_head_portrait = viewGroup.findViewById(R.id.start_head_portrait);//拍照
        start_photos = viewGroup.findViewById(R.id.start_photos);//图库按钮
        start_cancel = viewGroup.findViewById(R.id.start_cancel);//取消按钮
        dialog = new AlertDialog.Builder(Accessories_Add.this).create();//弹窗
        bitmap_imp = BitmapFactory.decodeResource(getResources(), R.drawable.loading_accessories);
        saveBitmapFile(bitmap_imp);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Accessories_Add.this, Clothe_Accessories.class));
        overridePendingTransition(R.anim.enter2, R.anim.quit2);
    }

    private void initView() {
        clothes_add_return.setOnClickListener(this);
        clothes_add_save.setOnClickListener(this);
        clothes_add_IMG.setOnClickListener(this);
        start_head_portrait.setOnClickListener(this);
        start_photos.setOnClickListener(this);
        start_cancel.setOnClickListener(this);
        clothes_add_IMG.setImageBitmap(bitmap_imp);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clothes_add_return:
                startActivity(new Intent(Accessories_Add.this, Clothe_Accessories.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.clothes_add_save:
                receiveMessage();//先接受数据
                break;
            case R.id.clothes_add_IMG:
                startDialog();
                break;
            case R.id.start_photos:
                choosePicture();//选择图片
                dialog.dismiss();
                break;
            case R.id.start_head_portrait:
                recentFile();//启动摄像头
                dialog.dismiss();
                break;
            case R.id.start_cancel://取消Dialog
                dialog.dismiss();
                break;
        }
    }

    /**
     * 接收编辑框中的数据
     */
    private void receiveMessage() {
        clothes_add_color_S = clothes_add_color.getText().toString();
        clothes_add_season_S = clothes_add_season.getText().toString();
        clothes_add_style_S = clothes_add_style.getText().toString();
        clothes_add_weather_S = clothes_add_weather.getText().toString();
        clothes_add_text_S = clothes_add_text.getText().toString();
        map.put("username", login_account_Ed);
        map.put("cloName", clothes_type);
        map.put("cloColor", clothes_add_color_S);
        map.put("cloSeason", clothes_add_season_S);
        map.put("cloStyle", clothes_add_style_S);
        map.put("cloWeather", clothes_add_weather_S);
        map.put("cloLabel", clothes_add_text_S);
        Log.d(TAG, "用户名：" + login_account_Ed
                + "类型:" + clothes_type
                + "颜色:" + clothes_add_color_S
                + "季节:" + clothes_add_season_S
                + "风格:" + clothes_add_style_S
                + "天气:" + clothes_add_weather_S
                + "便签:" + clothes_add_text_S
                + "图片" + outputImage);
        uploadClothes(outputImage, url + "LoginTest2/addUserClothes.action", map); //上传方法
    }


    /**
     * 显示dialog方法
     */
    private void startDialog() {
        Window window = dialog.getWindow();
        //设置显示动画
        window.setWindowAnimations(R.style.AppTheme);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.onWindowAttributesChanged(wl);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.getWindow().setContentView(viewGroup);
    }

    /**
     * 从图库中选择图片
     */
    private void choosePicture() {
        if (ContextCompat.checkSelfPermission(Accessories_Add.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Accessories_Add.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    /**
     * //打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);//打开相册
    }

    /**
     * 启动摄像头
     */
    private void recentFile() {
        outputImage = new File(getExternalCacheDir(), "head_portrait.jpg");
        Log.d(TAG, String.valueOf(getExternalCacheDir()));
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(Accessories_Add.this, "myself_mine_head_portrait", outputImage);

        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    /**
     * 拍照方法回退
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        clothes_add_IMG.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK)
                    //判断手机型号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4以上系统使用这个方法处理图片
                        handleImageOnKitkat(data);
                    } else {
                        //4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                break;
            default:
                break;
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

    /**
     * 4.4以上版本处理图片
     *
     * @param data
     */
    @TargetApi(19)
    private void handleImageOnKitkat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];   //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri,则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri,直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);//根据图片路径显示图片
    }

    /**
     * 4.4以下版本处理图片
     *
     * @param data
     */
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    /**
     * 根据图片路径显示图片
     *
     * @param imagePath
     */
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            clothes_add_IMG.setImageBitmap(bitmap);
            String temp[] = imagePath.replaceAll("\\\\", "/").split("/");
            String fileName = "";
            if (temp.length > 1) {
                fileName = temp[temp.length - 1];
                Log.d(TAG, fileName);
            }
            Log.d(TAG, imagePath);
            //获得图片路径
            try {
                outputImage = saveFile(bitmap, Environment.getExternalStorageDirectory().toString(), fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "无法得到图片", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取图片路径
     */
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 从相册中获取图片将Bitmap转换成文件
     * 保存文件
     *
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public File saveFile(Bitmap bm, String path, String fileName) throws IOException {
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(path, fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return myCaptureFile;
    }
}
