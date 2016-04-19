package com.yonyou.lxp.dmssy;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.utils.TbsLog;
import com.yonyou.lxp.dmssy.utils.X5WebView;

public class MainActivity extends AppCompatActivity {
//
//    private WebView webView;
//    private Context context;
//
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        context=this;
//        webView = (WebView) findViewById(R.id.webView);
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setSaveFormData(true);
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setAllowFileAccess(true);
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setSupportZoom(false);
//
//        webSettings.setAppCacheEnabled(true);
//        webView.setWebViewClient(new WebViewClient() {
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                if( url.startsWith("file:") || url.startsWith("https:") || url.startsWith("http:") ) {
//                    view.loadUrl(url);
//                    return true;
//                }
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                context.startActivity(intent);
//                return true;
//            }
//            @Override
//            public void onPageFinished(WebView view, String url)
//            {
//                super.onPageFinished(view, url);
//            }
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon)
//            {
//                super.onPageStarted(view, url, favicon);
//            }
//
//            @Override
//            public void onLoadResource(WebView view, String url) {
//                super.onLoadResource(view, url);
//            }
//
//        });
//        webView.setWebChromeClient(new WebChromeClient());
////        webView.loadUrl("file:///android_asset/JC_UI/login.html");
////        webView.loadUrl("http://222.180.239.10:9080/dcsapp/JC_UI/login.html");
//        webView.loadUrl("http://dms.changan.com.cn/cvs-app/SY_UI/login.html");
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
//            webView.goBack();// 返回前一个页面
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
public static final int MSG_OPEN_TEST_URL = 0;
    public static final int MSG_INIT_UI = 1;
    private final int mUrlStartNum = 0;
    private final int mUrlEndNum = 108;
    private int mCurrentUrl = mUrlStartNum;
    private boolean mNeedTestPage = false;
    private static final int MAX_LENGTH = 14;
    private ProgressBar mPageLoadingProgressBar;
    private X5WebView mWebView;
    private ViewGroup mViewParent;;
    private static final String mHomeUrl = "http://dms.changan.com.cn/cvs-app/SY_UI/login.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
                getWindow()
                        .setFlags(
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);
        mViewParent = (ViewGroup) findViewById(R.id.webView1);


        QbSdk.preInit(this);

        this.webViewTransportTest();

        mTestHandler.sendEmptyMessageDelayed(MSG_INIT_UI, 10);// �ӳ�1.5s����webview

    }

    private void webViewTransportTest() {
        X5WebView.setSmallWebViewEnabled(true);
    }

    private void initProgressBar() {
        mPageLoadingProgressBar = (ProgressBar) findViewById(R.id.progressBar1);// new
        // ProgressBar(getApplicationContext(),
        // null,
        // android.R.attr.progressBarStyleHorizontal);
        mPageLoadingProgressBar.setMax(100);
        mPageLoadingProgressBar.setProgressDrawable(this.getResources()
                .getDrawable(R.color.colorAccent));
    }

    private void init() {
        mWebView = new X5WebView(this);

        Log.w("grass", "Current SDK_INT:" + Build.VERSION.SDK_INT);


        mViewParent.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));

        initProgressBar();
        // ����Client
        mWebView.setWebViewClient(new com.tencent.smtt.sdk.WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView view, String url) {
                if (url.startsWith("file:") || url.startsWith("https:") || url.startsWith("http:")) {
                    view.loadUrl(url);
                    return true;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                MainActivity.this.startActivity(intent);
                return true;
            }

            @Override
            public com.tencent.smtt.export.external.interfaces.WebResourceResponse shouldInterceptRequest(com.tencent.smtt.sdk.WebView view,
                                                                                                          WebResourceRequest request) {
                // TODO Auto-generated method stub

                Log.e("should", "request.getUrl().toString() is " + request.getUrl().toString());

                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(com.tencent.smtt.sdk.WebView view, String url) {
                super.onPageFinished(view, url);
                mTestHandler.sendEmptyMessageDelayed(MSG_OPEN_TEST_URL, 5000);// 5s?
                if (Integer.parseInt(Build.VERSION.SDK) >= 16) ;

            }


        });

        mWebView.setWebChromeClient(new com.tencent.smtt.sdk.WebChromeClient() {
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//                TbsLog.d("", "title: " + title);
//                if (mUrl == null)
//                    return;
//                if (!mWebView.getUrl().equalsIgnoreCase(mHomeUrl)) {
//                    if (title != null && title.length() > MAX_LENGTH)
//                        mUrl.setText(title.subSequence(0, MAX_LENGTH) + "...");
//                    else
//                        mUrl.setText(title);
//                } else {
//                    mUrl.setText("");
//                }
//            }


            @Override
            public void onProgressChanged(com.tencent.smtt.sdk.WebView view, int newProgress) {
                // TODO Auto-generated method stub
                mPageLoadingProgressBar.setProgress(newProgress);
                if (mPageLoadingProgressBar != null && newProgress != 100) {
                    mPageLoadingProgressBar.setVisibility(View.VISIBLE);
                } else if (mPageLoadingProgressBar != null) {
                    mPageLoadingProgressBar.setVisibility(View.GONE);


                }
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String arg0, String arg1, String arg2,
                                        String arg3, long arg4) {
                TbsLog.d("", "url: " + arg0);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("�Ƿ�����")
                        .setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Toast.makeText(
                                                MainActivity.this,
                                                "fake message: i'll download...", Toast.LENGTH_LONG).show();
                                    }
                                })
                        .setNegativeButton("no",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        Toast.makeText(
                                                MainActivity.this,
                                                "fake message: refuse download...",
                                                Toast.LENGTH_LONG).show();
                                    }
                                })
                        .setOnCancelListener(
                                new DialogInterface.OnCancelListener() {

                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        // TODO Auto-generated method stub
                                        Toast.makeText(
                                                MainActivity.this,
                                                "fake message: refuse download...",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }).show();
            }
        });

        com.tencent.smtt.sdk.WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setDisplayZoomControls(false);
        webSetting.setUseWideViewPort(false);
        webSetting.setLoadWithOverviewMode(false);
        webSetting.setNeedInitialFocus(false);

        webSetting.setSupportMultipleWindows(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(com.tencent.smtt.sdk.WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(com.tencent.smtt.sdk.WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);

        long time = System.currentTimeMillis();
//        if (mIntentUrl == null) {
//            mWebView.loadUrl(mHomeUrl);
//        } else {
//            mWebView.loadUrl(mIntentUrl.toString());
//        }
        mWebView.loadUrl(mHomeUrl);
        TbsLog.e("time-cost", "cost time: "
                + (System.currentTimeMillis() - time));
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    private Handler mTestHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OPEN_TEST_URL:
                    if (!mNeedTestPage) {
                        return;
                    }

                    String testUrl = "file:///sdcard/outputHtml/html/"
                            + Integer.toString(mCurrentUrl) + ".html";
                    if (mWebView != null) {
                        mWebView.loadUrl(testUrl);
                    }

                    mCurrentUrl++;
                    break;
                case MSG_INIT_UI:
                    init();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

