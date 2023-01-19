package com.example.lockscreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.example.lockscreen.Models.Paras;
import com.example.lockscreen.Models.VideoWallPaperService;
import com.example.lockscreen.Utils.FileUtils;

import java.io.File;
import java.util.Objects;

public class MainActivity extends BaseActivity {
    private WebView webView;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Button choose_pic=findViewById(R.id.choose_pic);
        Button choose_vid=findViewById(R.id.choose_vid);
        Button setting=findViewById(R.id.setting);
        Button back=findViewById(R.id.backFir);
        webView=findViewById(R.id.webView);
        String[] mPermissionList = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        /*choose_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //在这里跳转到手机系统相册里面
                ActivityCompat.requestPermissions(MainActivity.this, mPermissionList, 100);
                getImage();
            }
        });*/
        choose_vid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(MainActivity.this, mPermissionList, 100);
                getVideo();
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetLockWallPaper();
            }
        });
        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setAllowContentAccess(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setBlockNetworkImage(true);

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SkipTo(FireworksActivity.class);
            }
        });
        //webSettings.setMediaPlaybackRequiresUserGesture(false);
        webView.setWebViewClient(new WebViewClient(){
            /**
             * 当前网页的链接仍在webView中跳转
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView webView,String url) {
                super.onPageFinished(webView,url);
                //imgReset();
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            /**
             * 显示自定义视图，无此方法视频不能播放
             */
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if(!Objects.equals(Paras.filesPath, "")) {
                        if(FileUtils.isVideoFileType(Paras.filesPath)&&Paras.isStopped) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    try {
                                        WebView webView=findViewById(R.id.webView);
                                        webView.loadUrl(Paras.filesPath);
                                        Paras.isStopped=false;
                                    } catch (Exception e) {

                                    }
                                }
                            });
                            /*try {
                                Thread.sleep(Paras.videoTime* 1000L);
                            } catch (Exception e) {

                            }*/
                        }
                        if(FileUtils.isImageFileType(Paras.filesPath)&&Paras.isStopped) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    try {
                                        File file=new File(Paras.filesPath);
                                        if(file.getPath().contains(".png.jpj")) {

                                        }
                                        WebView webView=findViewById(R.id.webView);
                                        webView.loadUrl(Paras.filesPath);
                                        Paras.isStopped=false;
                                    } catch (Exception e) {

                                    }
                                }
                            });
                        }
                    }

                }
            }
        }).start();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && null != data) {
            Uri uri = data.getData();
            if(DocumentsContract.isDocumentUri(this, uri)){
                String wholeID = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String id = wholeID.split(":")[1];
                    String selection = MediaStore.Video.Media._ID +"="+id;

                    String[] column = { MediaStore.Video.Media.DATA };
                    Cursor cursor = this.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, column,
                            selection, null, null);
                    int columnIndex = cursor.getColumnIndex(column[0]);
                    if (cursor.moveToFirst()) {
                        Paras.filesPath = cursor.getString(columnIndex);
                        Paras.videoTime=FileUtils.getLocalVideoDuration(Paras.filesPath);
                        Paras.isStopped=true;
                    }
                    cursor.close();
                }

            }
        }
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri uri = data.getData();
            if(DocumentsContract.isDocumentUri(this, uri)){
                String wholeID = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String id = wholeID.split(":")[1];
                    String selection = MediaStore.Images.Media._ID +"="+id;

                    String[] column = { MediaStore.Images.Media.DATA };
                    Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
                            selection, null, null);
                    int columnIndex = cursor.getColumnIndex(column[0]);
                    if (cursor.moveToFirst()) {
                        Paras.filesPath = cursor.getString(columnIndex);
                        Paras.isStopped=true;
                        Paras.videoTime=0;
                    }
                    cursor.close();
                }

            }
        }
    }

    private void getImage() {
        if(android.os.Build.BRAND.equals("Huawei")){
            Intent intentPic = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            this.startActivityForResult(intentPic,2);
        }
        if(android.os.Build.BRAND.equals("Xiaomi")){
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(Intent.createChooser(intent, "选择要导入的图片"), 1);
        }else {
            Intent intent = new Intent();
            if(Build.VERSION.SDK_INT < 19){
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
            }else {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
            }
            startActivityForResult(Intent.createChooser(intent, "选择要导入的图片"), 1);
        }
    }
    private void getVideo() {
        if(android.os.Build.BRAND.equals("Huawei")){
            Intent intentPic = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            this.startActivityForResult(intentPic,2);
        }
        if(android.os.Build.BRAND.equals("Xiaomi")){
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*");
            startActivityForResult(Intent.createChooser(intent, "选择要导入的视频"), 2);
        }else {
            Intent intent = new Intent();
            if(Build.VERSION.SDK_INT < 19){
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
            }else {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("video/*");
            }
            startActivityForResult(Intent.createChooser(intent, "选择要导入的视频"), 2);
        }
    }

    private void SetLockWallPaper() {
        // TODO Auto-generated method stub
        try {
            VideoWallPaperService.startWallPaper(MainActivity.this,Paras.filesPath);
            //Toast.makeText(MainActivity.this, "锁屏壁纸设置成功", Toast.LENGTH_SHORT).show();
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

/*    *//**
     * 读取图片资源JS
     *//*
    private void imgReset() {
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName('img'); " +
                "for(var i=0;i <objs.length;i++){"
                + "var img = objs[i]; " +
                " img.style.maxWidth = '100%'; img.style.height = 'auto'; " +
                "}" +
                "})()");
    }*/
}