package com.zdl.weiboshare;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.DownloadResponseHandler;

import java.io.File;

/**
 * Created by Administrator on 2018/4/24.
 */

class JavaScriptinterface {

    private Context context;

    public JavaScriptinterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void save(String imgurl) {

        MyOkHttp myOkHttp = MyApplication.getInstance().getMyOkHttp();

        myOkHttp.download()
                .url(imgurl)
                .filePath(Environment.getExternalStorageDirectory() + "/ahome/" + BitmapImageSave.generateFileName() + ".jpg")
                .tag(this)
                .enqueue(new DownloadResponseHandler() {
                    @Override
                    public void onStart(long totalBytes) {
                        Log.d("tag", "doDownload onStart");
                    }

                    @Override
                    public void onFinish(File downloadFile) {
                        Log.d("tag", "doDownload onFinish:");
                        Toast.makeText(context, "下载成功,在" + Environment.getExternalStorageDirectory() + "/ahome/" + "下查看", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(long currentBytes, long totalBytes) {
                        Log.d("tag", "doDownload onProgress:" + currentBytes + "/" + totalBytes);
                    }

                    @Override
                    public void onFailure(String error_msg) {
                        Log.d("tag", "doDownload onFailure:" + error_msg);
                    }
                });


    }
}
