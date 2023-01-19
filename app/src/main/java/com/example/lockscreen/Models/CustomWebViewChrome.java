package com.example.lockscreen.Models;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.lockscreen.R;

public abstract class CustomWebViewChrome extends WebChromeClient {

    private Activity currentActivity;

    public CustomWebViewChrome(WebView webView) {
        Context context = webView.getContext();
        if (context instanceof Activity) {
            currentActivity = (Activity) context;
        }
    }

    private ValueCallback<Uri[]> filePathCallback;

    public ValueCallback<Uri[]> getFilePathCallback() {
        return filePathCallback;
    }

    public void setFilePathCallback(ValueCallback<Uri[]> filePathCallback) {
        this.filePathCallback = filePathCallback;
    }

    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        this.filePathCallback = filePathCallback;
        String[] acceptTypes = fileChooserParams.getAcceptTypes();
        String acceptType = "*/*";
        StringBuilder sb = new StringBuilder();
        if (acceptTypes.length > 0) {
            for (String type : acceptTypes) {
                sb.append(type).append(';');
            }
        }
        if (sb.length() > 0) {
            String typeStr = sb.toString();
            acceptType = typeStr.substring(0, typeStr.length() - 1);
        }

        final String tempType = acceptType;

        showChooseDialog(tempType);

        return true;
    }

    private boolean isClickDialog = false;

    public boolean isClickDialog() {
        return isClickDialog;
    }

    public void setClickDialog(boolean clickDialog) {
        isClickDialog = clickDialog;
    }

    /**
     * 展示选择方式的对话框
     */
    private void showChooseDialog(String acceptType) {
        if (TextUtils.isEmpty(acceptType) || "image/*".equals(acceptType)) {
            String[] items = new String[]{"拍照","选择图片"};//创建item // "选择音频/录制音频",
            //添加列表
            AlertDialog alertDialog = new AlertDialog.Builder(currentActivity)
                    .setTitle("选择方式")
                    .setIcon(R.mipmap.ic_launcher)
                    .setItems(items, (dialogInterface, i) -> {
                        isClickDialog = true;
                        chooseFileFromWay(i);
                    })
                    .create();
            //加个监听，
            alertDialog.setOnCancelListener(dialogInterface -> {
                if (!isClickDialog) {
                    filePathCallback.onReceiveValue(null);
                } else {
                    //重置记录的状态
                    isClickDialog = false;
                }
            });
            alertDialog.show();
        }

    }

    public abstract void chooseFileFromWay(int i);
}
