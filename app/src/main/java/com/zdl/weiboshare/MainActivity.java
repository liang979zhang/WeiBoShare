package com.zdl.weiboshare;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.JsonParser;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.view.CropImageView;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.DownloadResponseHandler;
import com.zdl.weiboshare.Permission.PermissionResultCallBack;
import com.zdl.weiboshare.Permission.PermissionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private WebView collectWeb;
    //    private String adUrl = "http://192.168.252.111:8090/processImg/login.jsp";
    private String adUrl = "http://47.100.160.168:8083/processImg/login.jsp";
    private OpenFileWebChromeClient mOpenFileWebChromeClient;
    private long timeMillis;
    private PullToRefreshLayout pullToRefreshLayout;
    public static final int IMAGE_PICKER = 300;

    private Myhandler myhandler;
    private String[] urls;

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        collectWeb = findViewById(R.id.webview);
        pullToRefreshLayout = findViewById(R.id.pulltorefresh);
        pullToRefreshLayout.setCanLoadMore(false);
        myhandler = new Myhandler();
        loadingDialog = new LoadingDialog(this);
        imagepic();
        initper();
        webset();

        pull();

    }

    private void imagepic() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(false);  //显示拍照按钮
        imagePicker.setCrop(false);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(false); //是否按矩形区域保存
        imagePicker.setSelectLimit(200);    //选中数量限制
//        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
//        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
//        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
//        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
//        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    private void pull() {

        pullToRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                collectWeb.loadUrl(adUrl);

            }

            @Override
            public void loadMore() {
            }
        });
    }

    private void initper() {

        PermissionUtil.getInstance().request(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionResultCallBack() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionGranted(String... permissions) {
            }

            @Override
            public void onPermissionDenied(String... permissions) {
            }

            @Override
            public void onRationalShow(String... permissions) {
            }
        });
    }

    private void webset() {
        WebSettings settings = collectWeb.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
//        collectWeb.addJavascriptInterface(new JavaScriptinterface(this), "android");
        collectWeb.loadUrl(adUrl);
        mOpenFileWebChromeClient = new OpenFileWebChromeClient(this);
        collectWeb.setWebChromeClient(mOpenFileWebChromeClient);

        collectWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.contains("url")) {
                    dataset(url);
                    return true;
                }
                adUrl = url;
                return false;
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                mProgressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                mProgressDialog.hide();
                pullToRefreshLayout.finishRefresh();
            }

        });

        collectWeb.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (collectWeb.canGoBack()) {
                            if (adUrl.contains("login")) {
                                return false;
                            } else {
                                collectWeb.goBack();
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });
    }


    int state = -1;
    int total;

    private void dataset(String adUrl) {

        loadingDialog.show();
//        String substring = adUrl.substring(7, adUrl.length());
        String[] split = adUrl.split(";");
        urls = new String[split.length - 1];

        for (int i = 0; i < split.length - 1; i++) {
            urls[i] = split[i + 1];
        }
        state = 0;

        total = urls.length;


//        down(substring);


        myhandler.sendEmptyMessage(1005);


    }

    private void down(String urlimg) {

        MyOkHttp myOkHttp = MyApplication.getInstance().getMyOkHttp();

        myOkHttp.download()
                .url(urlimg)
                .filePath(Environment.getExternalStorageDirectory().getPath() + "/DCIM/" + BitmapImageSave.generateFileName() + ".jpg")
                .tag(this)
                .enqueue(new DownloadResponseHandler() {
                    @Override
                    public void onStart(long totalBytes) {
                        Log.d("tag", "doDownload onStart");
                    }

                    @Override
                    public void onFinish(File downloadFile) {
                        Log.d("tag", "doDownload onFinish:");
                        if (state == total) {
                            if (loadingDialog != null) {
                                loadingDialog.dismiss();
                            }

                            Toast.makeText(MainActivity.this, "下载成功,在" + Environment.getExternalStorageDirectory().getPath() + "/DCIM/" + "下查看", Toast.LENGTH_SHORT).show();
                        }
                        String string = downloadFile.toString();
                        try {
                            MediaStore.Images.Media.insertImage(MainActivity.this.getContentResolver(),
                                    downloadFile.getAbsolutePath(), downloadFile.getName(), null);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        // 最后通知图库更新
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(downloadFile);
                        intent.setData(uri);
                        MainActivity.this.sendBroadcast(intent);
                        myhandler.sendEmptyMessage(1005);
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


    private Map<String, String> getParamsMap(String url, String pre) {
        ArrayMap<String, String> queryStringMap = new ArrayMap<>();
        if (url.contains(pre)) {
            int index = url.indexOf(pre);
            int end = index + pre.length();
            String queryString = url.substring(end + 1);

            String[] queryStringSplit = queryString.split("&");

            String[] queryStringParam;
            for (String qs : queryStringSplit) {
                if (qs.toLowerCase().startsWith("data=")) {
                    //单独处理data项，避免data内部的&被拆分
                    int dataIndex = queryString.indexOf("data=");
                    String dataValue = queryString.substring(dataIndex + 5);
                    queryStringMap.put("data", dataValue);
                } else {
                    queryStringParam = qs.split("=");

                    String value = "";
                    if (queryStringParam.length > 1) {
                        //避免后台有时候不传值,如“key=”这种
                        value = queryStringParam[1];
                    }
                    queryStringMap.put(queryStringParam[0].toLowerCase(), value);
                }
            }
        }
        return queryStringMap;
    }


    private void parseCode(String code, String data) {
        if (code.equals("call")) {
            try {
                JSONObject json = new JSONObject(data);
                String phone = json.optString("data");
                //执行打电话的操作，具体代码省略
//                PhoneUtils.call(this, phone);
                Log.e("phone+++===", phone);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
    }

    public boolean isJson(String content) {
        try {
            new JsonParser().parse(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                callback(images);
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }


//            if (requestCode == OpenFileWebChromeClient.REQUEST_FILE_PICKER) {
//                if (mOpenFileWebChromeClient.mFilePathCallback != null) {
//                    Uri result = data == null || resultCode != Activity.RESULT_OK ? null
//                            : data.getData();
//                    if (result != null) {
//                        String path = MediaUtility.getPath(getApplicationContext(),
//                                result);
//                        Uri uri = Uri.fromFile(new File(path));
//                        mOpenFileWebChromeClient.mFilePathCallback
//                                .onReceiveValue(uri);
//                    } else {
//                        mOpenFileWebChromeClient.mFilePathCallback
//                                .onReceiveValue(null);
//                    }
//                }
//                if (mOpenFileWebChromeClient.mFilePathCallbacks != null) {
//                    Uri result = data == null || resultCode != Activity.RESULT_OK ? null
//                            : data.getData();
//                    if (result != null) {
//                        String path = MediaUtility.getPath(getApplicationContext(),
//                                result);
//                        Uri uri = Uri.fromFile(new File(path));
//                        mOpenFileWebChromeClient.mFilePathCallbacks
//                                .onReceiveValue(new Uri[]{uri});
//                    } else {
//                        mOpenFileWebChromeClient.mFilePathCallbacks
//                                .onReceiveValue(null);
//                    }
//                }
//
//                mOpenFileWebChromeClient.mFilePathCallback = null;
//                mOpenFileWebChromeClient.mFilePathCallbacks = null;
//
//
//            }
        }
    }

    private void callback(ArrayList<ImageItem> images) {
        Uri[] uris = new Uri[images.size()];
        for (int i = 0; i < images.size(); i++) {
            Uri uri = Uri.fromFile(new File(images.get(i).path));
            uris[i] = uri;
        }
        mOpenFileWebChromeClient.mFilePathCallbacks.onReceiveValue(uris);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - timeMillis) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                timeMillis = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);


    }


    class Myhandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);


            if (state <= total - 1) {
                down(urls[state]);
                state++;
            }

        }
    }
}

