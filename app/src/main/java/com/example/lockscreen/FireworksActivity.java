package com.example.lockscreen;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.example.lockscreen.Models.CustomWebViewChrome;
import com.example.lockscreen.Models.Paras;

import java.io.File;

public class FireworksActivity extends BaseActivity {
    private CustomWebViewChrome customWebViewChrome;
    private ValueCallback<Uri[]> filePathCallback;
    private Uri mUri;
    private Button btn;
    private boolean waitDouble = true;
    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fireworks);
        WebView webView=findViewById(R.id.webView);
        btn=  findViewById(R.id.back);
        btn.getBackground().setAlpha(0);
        /*// 隐藏状态栏
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);*/
        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }*/
        customWebViewChrome = new CustomWebViewChrome(webView) {
            @Override
            public void chooseFileFromWay(int i) {
                if (i == 0) {
                    showPhoto();
                } else if (i == 1) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, 15);
                }
            }
        };
        webView.setWebChromeClient(customWebViewChrome);
        WebSettings webSettings=webView.getSettings();
        //设置缓存
        webSettings.setSaveFormData(false);
        //设置JS支持
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        //设置支持缩放变焦
        webSettings.setBuiltInZoomControls(false);
        //设置是否支持缩放
        webSettings.setSupportZoom(false);
        //设置是否允许JS打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        // 修复一些机型webview无法点击
        webView.requestFocus(View.FOCUS_DOWN);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(waitDouble == true){
                    waitDouble = false;
                    Thread thread = new Thread(){
                        @Override
                        public void run(){
                            try {
                                sleep(2000);
                                if(waitDouble == false){
                                    waitDouble = true;
                                }
                            } catch (InterruptedException e) {

                            }
                        }
                    };
                    thread.start();
                }else{

                    waitDouble = true;
                    SkipTo(MainActivity.class);
                }
            }
        });
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(url.contains("lovefireworks/")) {
                    hideShowHello(view);
                }

            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                    return false;
                } else {
                    return true;
                }
            }
            @Override
            public void onLoadResource(WebView view, String url) {
                //在加载资源时过滤广告标签
                //对网页加载速度稍有影响
                if(url.contains("sweetalert.min.js")) {

                }
                hideMenu(view);
                hideCredits(view);
                //hideShowHello(view);
                super.onLoadResource(view, url);
            }



        });
        webView.loadUrl("muzihuaner.gitee.io/lovefireworks");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            filePathCallback = customWebViewChrome.getFilePathCallback();
            if (requestCode == 15) {
                if (resultCode == RESULT_OK) {
                    Uri imgUri = data.getData();
                    filePathCallback.onReceiveValue(new Uri[]{imgUri});
                } else {
                    filePathCallback.onReceiveValue(null);
                }
            } else if (requestCode == 8888) {//获取系统照片上传
                if (resultCode == RESULT_OK && mUri != null) {
                    filePathCallback.onReceiveValue(new Uri[]{mUri});
                } else {
                    filePathCallback.onReceiveValue(null);
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void showPhoto() {
        // 步骤一：创建存储照片的文件
        String mCurrentPath;
        //mCurrentPath = FileUtil.getImgpath(String.valueOf(System.currentTimeMillis()));
        File rootFile = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!rootFile.exists()) {
            boolean success = rootFile.mkdirs();
        }
        mCurrentPath = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg";
        File file = new File(mCurrentPath);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //步骤二：Android 7.0及以上获取文件 Uri
                mUri = FileProvider.getUriForFile(this, Paras.FILEPROVICE, file);
            } else {
                //步骤三：获取文件Uri
                mUri = Uri.fromFile(file);
            }
            //步骤四：调取系统拍照
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
            startActivityForResult(intent, 8888);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private void hideMenu(WebView view) {

        //JS，过滤带广告的标签(view-more)

        String javascript = "javascript:function hideMenu() {" +

                "var ViewMore = document.getElementsByClassName('menu__subheader');" +

                "var firstViewMore = ViewMore[0];" +

                "firstViewMore.remove();" + "}";

        view.loadUrl(javascript);

        view.loadUrl("javascript:hideMenu();");

    }
    private void hideCredits(WebView view) {

        //JS，过滤带广告的标签(view-more)

        String javascript = "javascript:function hideCredits() {" +

                "var ViewMore = document.getElementsByClassName('credits');" +

                "var firstViewMore = ViewMore[0];" +

                "firstViewMore.remove();" + "}";

        view.loadUrl(javascript);

        view.loadUrl("javascript:hideCredits();");

    }

    private void hideShowHello(WebView view) {

        //JS，过滤带广告的标签(view-more)

        String javascript = "javascript:setTimeout(\"hideShowHello()\",2000);" +
                "function hideShowHello() {" +
                /*"$(\".extra_information\").bind(\"DOMSubtreeModified\", function() {" +
                        "document.getElementsByClassName('swal-modal')[0].style.display='none';"+
                "})}";*/
                "var ViewMore = document.getElementsByClassName('swal-modal');" +
                "var firstViewMore = ViewMore[0];" +
                //"firstViewMore.style='display:none';";
                "firstViewMore.remove();" + "}";

        view.loadUrl(javascript);
        //view.loadUrl("javascript:");
        view.loadUrl("javascript:hideShowHello();");

    }
}