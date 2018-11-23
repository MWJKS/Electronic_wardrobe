package com.example.a10767.electronic_wardrobe.Mix_Manage;

/**
 * Created by 10767 on 2018/8/5.
 */

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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.a10767.electronic_wardrobe.ActivityCollector;
import com.example.a10767.electronic_wardrobe.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.a10767.electronic_wardrobe.Main_Fragment.MyselfMine.CHOOSE_PHOTO;
import static com.example.a10767.electronic_wardrobe.Main_Fragment.MyselfMine.TAKE_PHOTO;
import static com.example.a10767.electronic_wardrobe.StaticVariable.mixFrameList;
import static com.example.a10767.electronic_wardrobe.StaticVariable.mixPosition;
import static com.example.a10767.electronic_wardrobe.StaticVariable.mainBitmap;

/**
 * 所有框架活动
 */
public class Frame_All extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Frame_One";
    /*UI*/
    private Dialog dialog;
    private TextView clothes_choice_start;//拍照按钮
    private TextView clothes_choice_search;//图库按钮
    private TextView clothes_choice_cancel;//取消按钮
    private TextView clothes_choice_mine; //我的衣柜
    private ViewGroup viewGroup;
    private LinearLayout clothe_frame_return;
    private LinearLayout clothe_frame_next;
    private ListView frameListView;
    private Mix mix;
    private FrameAdapter frameAdapter;
    private Bitmap mixBitmap; //搭配bitmap
    /*上传*/
    private File outputImage;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clothe_frame);
        initUI();
        initView();
        ActivityCollector.addActivity(this);
        frameListView.setAdapter(frameAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Frame_All.this, Clothe_Mix.class));
        overridePendingTransition(R.anim.enter2, R.anim.quit2);

    }

    private void initView() {
        frameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mixPosition = i;
                startDialog();
            }
        });
        clothe_frame_return.setOnClickListener(this);
        clothe_frame_next.setOnClickListener(this);
        clothes_choice_start.setOnClickListener(this);
        clothes_choice_search.setOnClickListener(this);
        clothes_choice_cancel.setOnClickListener(this);
        clothes_choice_mine.setOnClickListener(this);
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

    private void initUI() {
        frameAdapter = new FrameAdapter(Frame_All.this, mixFrameList);
        frameListView = findViewById(R.id.frame_listView);
        clothe_frame_return = findViewById(R.id.clothe_frame_return);
        clothe_frame_next = findViewById(R.id.clothe_frame_next);

        viewGroup = (ViewGroup) LayoutInflater.from(Frame_All.this).inflate(R.layout.clothes_choice_dialog, null);
        clothes_choice_start = viewGroup.findViewById(R.id.clothes_choice_start);//拍照
        clothes_choice_search = viewGroup.findViewById(R.id.clothes_choice_search);//图库按钮
        clothes_choice_cancel = viewGroup.findViewById(R.id.clothes_choice_cancel);//取消按钮
        clothes_choice_mine = viewGroup.findViewById(R.id.clothes_choice_mine); //我的衣柜

        dialog = new AlertDialog.Builder(Frame_All.this).create();//弹窗
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clothe_frame_return: //返回
                startActivity(new Intent(Frame_All.this, Clothe_Mix.class));
                overridePendingTransition(R.anim.enter2, R.anim.quit2);
                break;
            case R.id.clothes_choice_search:
                choosePicture();//选择图片
                dialog.dismiss();
                break;
            case R.id.clothes_choice_start:
                recentFile();//启动摄像头
                dialog.dismiss();
                break;
            case R.id.clothes_choice_mine://从我的衣柜中查找
                startActivity(new Intent(Frame_All.this, Wardrobe_choice.class));
                dialog.dismiss();
                break;
            case R.id.clothes_choice_cancel://取消Dialog
                dialog.dismiss();
                break;
            case R.id.clothe_frame_next: //下一步
                //合成所有图片
                int i = mixFrameList.size();
                if (i == 4) {
                    mainBitmap = MixBitmap.newBitmap(MixBitmap.newBitmap(MixBitmap.newBitmap(mixFrameList.get(0).getBitmap(), mixFrameList.get(1).getBitmap()), mixFrameList.get(2).getBitmap()), mixFrameList.get(3).getBitmap());
                    startActivity(new Intent(Frame_All.this, Mix_Add.class));
                    overridePendingTransition(R.anim.enter, R.anim.quit);
                }
                if (i == 3) {
                    mainBitmap = MixBitmap.newBitmap(MixBitmap.newBitmap(mixFrameList.get(0).getBitmap(), mixFrameList.get(1).getBitmap()), mixFrameList.get(2).getBitmap());
                    startActivity(new Intent(Frame_All.this, Mix_Add.class));
                    overridePendingTransition(R.anim.enter, R.anim.quit);
                }
                if (i == 2) {
                    mainBitmap = MixBitmap.newBitmap(mixFrameList.get(0).getBitmap(), mixFrameList.get(1).getBitmap());
                    startActivity(new Intent(Frame_All.this, Mix_Add.class));
                    overridePendingTransition(R.anim.enter, R.anim.quit);
                }
                break;

        }
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
            imageUri = FileProvider.getUriForFile(Frame_All.this, "myself_mine_head_portrait", outputImage);

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
        if (ContextCompat.checkSelfPermission(Frame_All.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Frame_All.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
                        mixBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        mix = new Mix(mixBitmap);
                        mixFrameList.set(mixPosition, mix);
                        frameAdapter.notifyDataSetChanged();
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
            mixBitmap = BitmapFactory.decodeFile(imagePath);
            mix = new Mix(mixBitmap);
            mixFrameList.set(mixPosition, mix);
            frameAdapter.notifyDataSetChanged();
            String temp[] = imagePath.replaceAll("\\\\", "/").split("/");
            String fileName = "";
            if (temp.length > 1) {
                fileName = temp[temp.length - 1];
                Log.d(TAG, fileName);
            }
            Log.d(TAG, imagePath);
            //获得图片路径
            try {
                outputImage = saveFile(mixBitmap, Environment.getExternalStorageDirectory().toString(), fileName);
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

}
