package com.example.a10767.electronic_wardrobe.Others_Wardrobe;

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

import static com.example.a10767.electronic_wardrobe.Main_Fragment.MyselfMine.CHOOSE_PHOTO;
import static com.example.a10767.electronic_wardrobe.Main_Fragment.MyselfMine.TAKE_PHOTO;
import static com.example.a10767.electronic_wardrobe.StaticVariable.login_account_Ed;
import static com.example.a10767.electronic_wardrobe.StaticVariable.othersBitmap;
import static com.example.a10767.electronic_wardrobe.StaticVariable.url;

/**
 * Created by 10767 on 2018/8/27.
 */

public class Others_Add extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Others_Add";
    //自身UI
    private LinearLayout others_add_return;// 返回
    private LinearLayout others_add_IMG_L; //图片_L
    private ImageView others_add_IMG; //图片
    private EditText others_add_text; //内容
    private String others_add_text_S; //内容S
    private Button others_add_report; //发表
    //    dialog按钮
    private Dialog dialog;
    private TextView others_clothes_start;//拍照按钮
    private TextView others_clothes_search;//图库按钮
    private TextView others_clothes_cancel;//取消按钮
    private TextView others_clothes_mine; //我的衣柜
    private TextView others_clothes_suit; //我的套装
    private ViewGroup viewGroup;
    //上传UI
    private File outputImage;
    private Uri imageUri;
    private Bitmap bitmap_imp; //初始化图片
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
                    Toast.makeText(Others_Add.this, "发表成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Others_Add.this, OthersWardrobe.class));
                    overridePendingTransition(R.anim.enter2, R.anim.enter2);
                    break;
                case UPLOAD_FAIL:
                    Toast.makeText(Others_Add.this, "网络连接诶错误,上传失败", Toast.LENGTH_SHORT).show();
                    break;
                case FILE_ERROR:
                    Toast.makeText(Others_Add.this, "请选择图片", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Others_Add.this, OthersWardrobe.class));
        overridePendingTransition(R.anim.enter2, R.anim.quit2);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.others_add);
        initUI();
        initView();
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void initView() {
        others_add_return.setOnClickListener(this);
        others_add_IMG_L.setOnClickListener(this);
        others_add_report.setOnClickListener(this);
        others_clothes_start.setOnClickListener(this);
        others_clothes_search.setOnClickListener(this);
        others_clothes_cancel.setOnClickListener(this);
        others_clothes_mine.setOnClickListener(this);
        others_clothes_suit.setOnClickListener(this);
        others_add_IMG.setImageBitmap(othersBitmap);
    }

    private void initUI() {
        others_add_return = findViewById(R.id.others_add_return);
        others_add_IMG_L = findViewById(R.id.others_add_IMG_L);
        others_add_text = findViewById(R.id.others_add_text);
        others_add_IMG = findViewById(R.id.others_add_IMG);
        others_add_report = findViewById(R.id.others_add_report);

        viewGroup = (ViewGroup) LayoutInflater.from(Others_Add.this).inflate(R.layout.others_clothes_dialog, null);
        others_clothes_start = viewGroup.findViewById(R.id.others_clothes_start);//拍照
        others_clothes_search = viewGroup.findViewById(R.id.others_clothes_search);//图库按钮
        others_clothes_cancel = viewGroup.findViewById(R.id.others_clothes_cancel);//取消按钮
        others_clothes_mine = viewGroup.findViewById(R.id.others_clothes_mine); //我的衣柜
        others_clothes_suit = viewGroup.findViewById(R.id.others_clothes_suit); //我的套装

        dialog = new AlertDialog.Builder(Others_Add.this).create();//弹窗

        if (othersBitmap.sameAs(BitmapFactory.decodeResource(getResources(), R.drawable.others_add))) {
            bitmap_imp = BitmapFactory.decodeResource(getResources(), R.drawable.empty);
            saveBitmapFile(bitmap_imp);
        } else {
            saveBitmapFile(othersBitmap);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.others_add_return:
                startActivity(new Intent(Others_Add.this, OthersWardrobe.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.others_add_IMG_L:
                startDialog();
                break;
            case R.id.others_clothes_start: //拍照
                recentFile();
                dialog.dismiss();
                break;
            case R.id.others_clothes_search://图库
                choosePicture();
                dialog.dismiss();
                break;
            case R.id.others_clothes_mine: //我的衣柜
                startActivity(new Intent(Others_Add.this, Others_Wardrobe_Choice.class));
                dialog.dismiss();
                break;
            case R.id.others_clothes_suit: //我的套装
                startActivity(new Intent(Others_Add.this, My_Suit.class));
                dialog.dismiss();
                break;
            case R.id.others_clothes_cancel: //取消
                dialog.dismiss();
                break;
            case R.id.others_add_report: //发表内容
                receiveMessage();
                break;
        }

    }

    /**
     * 接受数据
     */
    private void receiveMessage() {
        others_add_text_S = others_add_text.getText().toString();
        map.put("othertext", others_add_text_S);
        map.put("username", login_account_Ed);
        Log.d(TAG, "发表内容：" + others_add_text_S);
        if (!TextUtils.isEmpty(others_add_text_S)) {
            uploadClothes(outputImage, url + "LoginTest2/addOtherInformation.action", map); //上传方法
        } else {
            Toast.makeText(Others_Add.this, "发表内容不能为空", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 显示弹窗
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
            imageUri = FileProvider.getUriForFile(Others_Add.this, "myself_mine_head_portrait", outputImage);

        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    /**
     * 从图库中选择图片
     */
    private void choosePicture() {
        if (ContextCompat.checkSelfPermission(Others_Add.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Others_Add.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
     * 方法回退
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将拍摄的照片显示出来
                        othersBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        others_add_IMG.setImageBitmap(othersBitmap);
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
            othersBitmap = BitmapFactory.decodeFile(imagePath);
            others_add_IMG.setImageBitmap(othersBitmap);
            String temp[] = imagePath.replaceAll("\\\\", "/").split("/");
            String fileName = "";
            if (temp.length > 1) {
                fileName = temp[temp.length - 1];
                Log.d(TAG, fileName);
            }
            Log.d(TAG, imagePath);
            //获得图片路径
            try {
                outputImage = saveFile(othersBitmap, Environment.getExternalStorageDirectory().toString(), fileName);
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
            sb.append("Content-Disposition:form-data; name=\"" + "otherpicture"
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
