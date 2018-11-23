package com.example.a10767.electronic_wardrobe.Main_Fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.Fix_Message.FixPassword;
import com.example.a10767.electronic_wardrobe.Fix_Message.FixPhone;
import com.example.a10767.electronic_wardrobe.Fix_Message.MyselfGender;
import com.example.a10767.electronic_wardrobe.Fix_Message.MyselfUsername;
import com.example.a10767.electronic_wardrobe.OkHttpUtil;
import com.example.a10767.electronic_wardrobe.R;

import org.json.JSONException;
import org.json.JSONObject;

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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


import static com.example.a10767.electronic_wardrobe.StaticVariable.login_account_Ed;
import static com.example.a10767.electronic_wardrobe.StaticVariable.themain;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;
/**
 * Created by 10767 on 2018/7/3.
 */

/**
 * 我的信息中的个人信息
 */
public class MyselfMine extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MyselfMine";
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private Uri imageUri;
    private ProgressDialog progressDialog; //等待
    private Dialog dialog;
    private ViewGroup viewGroup;
    private RelativeLayout myself_exit;
    private LinearLayout myself_mine_return; //返回按钮
    private ImageView picture; //头像图片
    private LinearLayout myself_mine_head_portrait;//头像按钮
    private TextView start_head_portrait;//拍照按钮
    private TextView start_photos;//图库按钮
    private TextView start_cancel;//取消按钮
    private LinearLayout myself_username;//用户名按钮
    private LinearLayout myself_gerder;//性别按钮
    private LinearLayout myself_changePassword; //修改密码
    private LinearLayout myself_phone;// 修改手机号
    private TextView username_show; //用户名显示
    private TextView phone_show; //电话显示
    private TextView gender_show;//性别显示

    private Bitmap bitmap_main;
    private String username_show_receive = "未绑定"; //字符串用户名显示
    private String gender_show_receive = "未绑定";  //字符串性别显示
    private String phone_show_receive = "未绑定";  //字符安川电话显示

    private final int SHOW_ERROR = -1;
    private final int SHOW_SUCCESS = 1;
    private File outputImage;

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

    private Map<String, String> map = new HashMap<String, String>();


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_ERROR:
                    progressDialog.dismiss();
                    Toast.makeText(MyselfMine.this, "网络连接异常，无法显示信息", Toast.LENGTH_SHORT).show();
                    break;
                case SHOW_SUCCESS:
                    username_show.setText(username_show_receive);
                    gender_show.setText(gender_show_receive);
                    phone_show.setText(phone_show_receive);
                    picture.setImageBitmap(bitmap_main);
                    progressDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myself_mine);
        initUI();
        initView();
        showMessage();//显示个人信息
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        themain = 2;
        startActivity(new Intent(MyselfMine.this, TheMain.class));
        overridePendingTransition(R.anim.enter2, R.anim.quit2);
    }

    /**
     * 显示个人信息
     */
    private void showMessage() {
        OkHttpUtil.postJson_showMessage(url + "LoginTest2/personinformation.action", login_account_Ed, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(SHOW_ERROR);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, login_account_Ed);
                String show_message = response.body().string();
                Log.d(TAG, show_message);
                try {
                    JSONObject jsonObject = new JSONObject(show_message);
                    if (!jsonObject.getString("useralias").equals("")) {
                        username_show_receive = jsonObject.getString("useralias");
                    }
                    if (!jsonObject.getString("grade").equals("")) {
                        gender_show_receive = jsonObject.getString("grade");
                    }
                    if (!jsonObject.getString("phone").equals("")) {
                        phone_show_receive = jsonObject.getString("phone");
                    }
                    if (!jsonObject.getString("photo").equals("")) {
                        bitmap_main = getBitmap(jsonObject.getString("photo"));
                    }
                    if (jsonObject.getString("photo").equals("")) {
                        bitmap_main = BitmapFactory.decodeResource(getResources(), R.drawable.myself_message_mine);
                    }
                    handler.sendEmptyMessage(SHOW_SUCCESS);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void initUI() {
        progressDialog = new ProgressDialog(MyselfMine.this);
        progressDialog.setMessage("加载中....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        myself_exit = findViewById(R.id.myself_exit);
        username_show = findViewById(R.id.username_show);
        phone_show = findViewById(R.id.phone_show);
        gender_show = findViewById(R.id.gender_show);
        myself_mine_return = findViewById(R.id.myself_mine_return);
        picture = findViewById(R.id.myself_message_picture);
        myself_gerder = findViewById(R.id.myself_gender);
        myself_username = findViewById(R.id.myself_username);
        myself_changePassword = findViewById(R.id.myself_changePassword); //修改密码
        myself_phone = findViewById(R.id.myself_phone);//修改手机号
        myself_mine_head_portrait = findViewById(R.id.myself_mine_head_portrait);
        viewGroup = (ViewGroup) LayoutInflater.from(MyselfMine.this).inflate(R.layout.head_portrait_dialog, null);
        dialog = new AlertDialog.Builder(MyselfMine.this).create();//弹窗
        start_head_portrait = viewGroup.findViewById(R.id.start_head_portrait);//拍照
        start_photos = viewGroup.findViewById(R.id.start_photos);//图库按钮
        start_cancel = viewGroup.findViewById(R.id.start_cancel);//取消按钮
    }

    private void initView() {
        myself_mine_return.setOnClickListener(this);//返回
        myself_mine_head_portrait.setOnClickListener(this);//个人信息
        start_head_portrait.setOnClickListener(this);//拍照
        start_photos.setOnClickListener(this);//图库
        start_cancel.setOnClickListener(this);//取消
        myself_exit.setOnClickListener(this);
        myself_username.setOnClickListener(this);//用户名
        myself_gerder.setOnClickListener(this);//性别
        myself_changePassword.setOnClickListener(this); //修改密码
        myself_phone.setOnClickListener(this);//修改手机号
        map.put("username", login_account_Ed);//向集合中添加
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.myself_mine_return://返回按钮
                themain = 2;
                startActivity(new Intent(MyselfMine.this, TheMain.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.myself_mine_head_portrait: //头像按钮
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
            case R.id.myself_gender://性别
                Intent intent_gender = new Intent(MyselfMine.this, MyselfGender.class);
                startActivity(intent_gender);
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.myself_username://用户名
                Intent intent_username = new Intent(MyselfMine.this, MyselfUsername.class);
                startActivity(intent_username);
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.myself_changePassword: //修改密码
                Intent intent_fix_password = new Intent(MyselfMine.this, FixPassword.class);
                startActivity(intent_fix_password);
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.myself_phone: //修改电话号码
                Intent intent_fix_phone = new Intent(MyselfMine.this, FixPhone.class);
                startActivity(intent_fix_phone);
                overridePendingTransition(R.anim.enter, R.anim.quit);
                break;
            case R.id.myself_exit:
                ActivityCollector.finaishAll();
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
        }
    }

    /**
     * 从图库中选择图片
     */
    private void choosePicture() {
        if (ContextCompat.checkSelfPermission(MyselfMine.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MyselfMine.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
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
            imageUri = FileProvider.getUriForFile(MyselfMine.this, "myself_mine_head_portrait", outputImage);

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
                        picture.setImageBitmap(bitmap);
                        uploadFile(outputImage, url + "LoginTest2/personbyphoto.action", map); //上传头像方法
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
     * 相册方法回退
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(MyselfMine.this, "请打开访问权限，在设置中打开", Toast.LENGTH_SHORT).show();
                }
                break;
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
     * 根据图片路径显示图片
     *
     * @param imagePath
     */
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
            String temp[] = imagePath.replaceAll("\\\\", "/").split("/");
            String fileName = "";
            if (temp.length > 1) {
                fileName = temp[temp.length - 1];
                Log.d(TAG, fileName);
            }
            Log.d(TAG, imagePath);

            //获得图片路径
            try {
                File file = saveFile(bitmap, Environment.getExternalStorageDirectory().toString(), fileName);
                uploadFile(file, url + "LoginTest2/personbyphoto.action", map);
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(this, "无法得到图片", Toast.LENGTH_SHORT).show();
        }
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

    /**
     * 获取头像
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
     * 上传头像参数
     *
     * @param file
     * @param RequestURL
     * @param param
     */
    public void uploadFile(final File file, final String RequestURL, final Map<String, String> param) {
        if (file == null || (!file.exists())) {
            Log.d(TAG, "文件不存在");
            return;
        }

        Log.i(TAG, "请求的URL=" + RequestURL);
        Log.i(TAG, "请求的fileName=" + file.getName());

        new Thread(new Runnable() { //开启线程上传文件
            @Override
            public void run() {
                toUploadFile(file, RequestURL, param);
            }
        }).start();

    }


    /**
     * 上传头像
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
            // conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

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
            sb.append("Content-Disposition:form-data; name=\"" + "photo"
                    + "\"; filename=\"" + file.getName() + "\"" + LINE_END);
            sb.append("Content-Type:image/pjpeg" + LINE_END); // 这里配置的Content-type很重要的 ，用于服务器端辨别文件的类型的
            sb.append(LINE_END);
            params = sb.toString();
            sb = null;
            Log.i(TAG, file.getName() + "=" + params + "##");
            dos.write(params.getBytes());
                         /*上传文件*/
            InputStream is = new FileInputStream(file);
            //            onUploadProcessListener.initUpload((int) file.length());
            byte[] bytes = new byte[1024];
            int len = 0;
            int curLen = 0;
            while ((len = is.read(bytes)) != -1) {
                curLen += len;
                dos.write(bytes, 0, len);
                //                onUploadProcessListener.onUploadProcess(curLen);
            }
            is.close();

            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
            dos.write(end_data);
            dos.flush();
            //
            // dos.write(tempOutputStream.toByteArray());
            /**
             * 获取响应码 200=成功 当响应成功，获取响应的流
             */
            int res = conn.getResponseCode();
            responseTime = System.currentTimeMillis();
            this.requestTime = (int) ((responseTime - requestTime) / 1000);
            Log.e(TAG, "response code:" + res);
            if (res == 200) {
                Log.e(TAG, "request success");
                InputStream input = conn.getInputStream();
                StringBuffer sb1 = new StringBuffer();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                String s = sb1.toString();
                result = s;
                Log.e(TAG, "result : " + result);
                Log.d(TAG, "上传结果" + result);
                return;
            } else {
                Log.e(TAG, "request error" + res);
                Log.d(TAG, "上传失败：code=" + res);
                handler.sendEmptyMessage(SHOW_ERROR);
                return;
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, "上传失败：error=" + e.getMessage());
            e.printStackTrace();
            handler.sendEmptyMessage(SHOW_ERROR);
            return;
        } catch (IOException e) {
            Log.d(TAG, "上传失败：error=" + e.getMessage());
            e.printStackTrace();
            handler.sendEmptyMessage(SHOW_ERROR);
            return;
        }
    }
}
