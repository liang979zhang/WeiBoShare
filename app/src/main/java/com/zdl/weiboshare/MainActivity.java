package com.zdl.weiboshare;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.zdl.weiboshare.Permission.PermissionResultCallBack;
import com.zdl.weiboshare.Permission.PermissionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private WebView collectWeb;
    private String adUrl = "http://192.168.252.111:8090/processImg/login.jsp";
    private OpenFileWebChromeClient mOpenFileWebChromeClient;
    private long timeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        collectWeb = findViewById(R.id.webview);

        initper();
        webset();
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
        collectWeb.addJavascriptInterface(new JavaScriptinterface(this),
                "android");
        collectWeb.loadUrl(adUrl);
        mOpenFileWebChromeClient = new OpenFileWebChromeClient(this);
        collectWeb.setWebChromeClient(mOpenFileWebChromeClient);

        collectWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

//                Map<String, String> map = getParamsMap(url, "protocol://android");
//                String code = map.get("code");
//                String data = map.get("data");
//                Log.e("data+++===", data);
//                parseCode(code, data);
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

        if (requestCode == OpenFileWebChromeClient.REQUEST_FILE_PICKER) {
            if (mOpenFileWebChromeClient.mFilePathCallback != null) {
                Uri result = data == null || resultCode != Activity.RESULT_OK ? null
                        : data.getData();
                if (result != null) {
                    String path = MediaUtility.getPath(getApplicationContext(),
                            result);
                    Uri uri = Uri.fromFile(new File(path));
                    mOpenFileWebChromeClient.mFilePathCallback
                            .onReceiveValue(uri);
                } else {
                    mOpenFileWebChromeClient.mFilePathCallback
                            .onReceiveValue(null);
                }
            }
            if (mOpenFileWebChromeClient.mFilePathCallbacks != null) {
                Uri result = data == null || resultCode != Activity.RESULT_OK ? null
                        : data.getData();
                if (result != null) {
                    String path = MediaUtility.getPath(getApplicationContext(),
                            result);
                    Uri uri = Uri.fromFile(new File(path));
                    mOpenFileWebChromeClient.mFilePathCallbacks
                            .onReceiveValue(new Uri[]{uri});
                } else {
                    mOpenFileWebChromeClient.mFilePathCallbacks
                            .onReceiveValue(null);
                }
            }

            mOpenFileWebChromeClient.mFilePathCallback = null;
            mOpenFileWebChromeClient.mFilePathCallbacks = null;


        }
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
}
