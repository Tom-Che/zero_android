package com.che.zero.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Che on 2016/07/25.
 */
public class ApplicationEx extends Application {

    //所有的activity列表
    private List<Activity> activities;

    public void addActivities(Activity a) {
        activities.add(a);
    }

    public void removeActivities(Activity a) {
        activities.remove(a);
    }

    //超出65K方法限制分包
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        activities = new ArrayList<>();
    }
}
