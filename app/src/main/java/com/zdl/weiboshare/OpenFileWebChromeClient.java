package com.zdl.weiboshare;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.lzy.imagepicker.ui.ImageGridActivity;

import static com.zdl.weiboshare.MainActivity.IMAGE_PICKER;

/**
 * Created by Administrator on 2018/4/21.
 */

public class OpenFileWebChromeClient extends WebChromeClient {
    public ValueCallback<Uri> mFilePathCallback;
    public ValueCallback<Uri[]> mFilePathCallbacks;
    public static final int REQUEST_FILE_PICKER = 1;

    private Activity context;

    public OpenFileWebChromeClient(MainActivity mainActivity) {
        this.context = mainActivity;
    }
//

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        mFilePathCallbacks = filePathCallback;


//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("*/*");
//        context.startActivityForResult(Intent.createChooser(intent, "File Chooser"),
//                REQUEST_FILE_PICKER);

        Intent intent = new Intent(context, ImageGridActivity.class);
        context.startActivityForResult(intent, IMAGE_PICKER);

        return true;
    }

    //openFileChooser 方法是隐藏方法
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {// android 系统版本>4.1.1
//                showDialog();
        mFilePathCallback = uploadMsg;


        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        context.startActivityForResult(Intent.createChooser(intent, "File Chooser"),
                REQUEST_FILE_PICKER);
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg) {//android 系统版本<3.0
//                showDialog();
        mFilePathCallback = uploadMsg;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        context.startActivityForResult(Intent.createChooser(intent, "File Chooser"),
                REQUEST_FILE_PICKER);
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {//android 系统版本3.0+
//                showDialog();
        mFilePathCallback = uploadMsg;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        context.startActivityForResult(Intent.createChooser(intent, "File Chooser"),
                REQUEST_FILE_PICKER);
    }

}
