package com.zdl.weiboshare;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.GsonResponseHandler;
import com.zdl.weiboshare.Permission.PermissionResultCallBack;
import com.zdl.weiboshare.Permission.PermissionUtil;

import java.io.File;
import java.util.ArrayList;

public class ImageLoadActivity extends AppCompatActivity {


    ImageView iv_image;

    private ImageLoadActivity instance;
    private static final int IMAGE_PICKER = 300;
    private String headImage;
    private MyOkHttp mMyOkhttp;
    private String url = "http://192.168.252.111:8090/processImg/pictures";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_load);
        instance = this;
        iv_image = findViewById(R.id.iv_image);
        mMyOkhttp = new MyOkHttp();
        initWight();

        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPer()) {
                    Intent intent = new Intent(instance, ImageGridActivity.class);
                    startActivityForResult(intent, IMAGE_PICKER);
                }
            }
        });

    }

    boolean ischeck = false;

    private boolean checkPer() {

        PermissionUtil.getInstance().request(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionResultCallBack() {
            @Override
            public void onPermissionGranted() {
                ischeck = true;

            }

            @Override
            public void onPermissionGranted(String... permissions) {
            }

            @Override
            public void onPermissionDenied(String... permissions) {
                ischeck = false;
            }

            @Override
            public void onRationalShow(String... permissions) {
                ischeck = false;
            }
        });

        return ischeck;
    }

    private void initWight() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(false); //是否按矩形区域保存
        imagePicker.setSelectLimit(1);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
        imagePicker.setMultiMode(false);   //允许剪切
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);

                for (ImageItem datae : images) {
                    headImage = datae.path;
//                    setLoadImage(headImage,41);

                }

                setLoadImage2(headImage, 41);



//                LoadHeadImage(headImage);

            } else {
                Toast.makeText(instance, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setLoadImage2(String headImage, int i) {
//        String url = ApiService.BASE_URL + "index.php?app=api&mod=User&act=upload_face";
//        postFile(url, map, file, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e("tag", call.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
////                Log.e("tag", response.body().string().toString());
//                Message message = new Message();
//                message.what = 101;
//                message.obj = response.body().string().toString();
//                myHandler.sendMessage(message);
//
//            }
//        });


        mMyOkhttp.upload()
                .url(url)
                .addParam("messageType", "messageTypetest")
                .addParam("content", "testcontent")
                .addFile("picturefile", new File(headImage))        //上传已经存在的File
//                .addFile("avatar2", "asdsda.png", byteContents)    //直接上传File bytes
                .tag(this)
                .enqueue(new GsonResponseHandler<UploadModel>() {
                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        Log.d("tag", "doUpload onFailure:" + error_msg);
                    }

                    @Override
                    public void onProgress(long currentBytes, long totalBytes) {
                        Log.d("tag", "doUpload onProgress:" + currentBytes + "/" + totalBytes);
                    }

                    @Override
                    public void onSuccess(int statusCode, UploadModel response) {
                        Log.d("tag", "doUpload onSuccess:" + response.getData().toString());
                    }
                });
    }
}
