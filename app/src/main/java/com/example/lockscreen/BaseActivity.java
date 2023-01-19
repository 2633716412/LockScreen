package com.example.lockscreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;


import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;



public class BaseActivity extends Activity {
    public static BaseActivity currActivity = null;

    private static final ArrayList<BaseActivity> activitys = new ArrayList<>();

    private static final ReentrantLock lock = new ReentrantLock();

    protected Context GetContext() {
        return this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currActivity = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                DestoryActivitise(currActivity);
            }
        }).start();
    }

    static private synchronized void DestoryActivitise(BaseActivity newBaseActivity) {

        lock.lock();

        try {
            for (BaseActivity ba : activitys) {
                try {
                    ba.finish();
                } catch (Exception ex) {

                }
            }
        } catch (Exception ex) {

        }

        activitys.add(newBaseActivity);

        lock.unlock();
    }

    @Override
    protected void onDestroy() {
        lock.lock();
        activitys.remove(this);
        lock.unlock();
        super.onDestroy();
    }


    public static void SkipTo(Class<? extends Activity> cls) {

        try {

            if (currActivity != null) {
                currActivity.Onleave();
            }

            Intent i = new Intent(currActivity, cls);
            currActivity.startActivity(i);
        } catch (Exception ex) {

        }
    }

    public void UIAction(Runnable runnable) {
        this.runOnUiThread(runnable);
    }

    public void Onleave() {

    }
}
