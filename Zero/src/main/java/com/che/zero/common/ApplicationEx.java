package com.che.zero.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.che.acommon.guava.Strings;
import com.che.acommon.util.AppUtil;
import com.che.zero.activity.WelcomeActivity;
import com.che.zero.db.DbManage;
import com.che.zero.db.ZeroDbAdapter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Che on 2016/07/25.
 */
public class ApplicationEx extends Application {
    private static final String TAG = "ApplicationEx";

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

        String pname = AppUtil.getProcessName(getApplicationContext());//获取当前进程名
        if (Strings.isNullOrEmpty(pname) || pname.lastIndexOf("baidu") > 0) {
            return;
        }
        Log.e(TAG, "----------------ApplicationEx Init Start------------------");

        ClientConstant.PATH_DB_NAME = getExternalCacheDir().getPath() + "/db";//本地数据库
        ClientConstant.PATH_IMAGE_TEMP = getExternalCacheDir().getPath() + "/imageTemp/";//图片缓存
        ClientConstant.PATH_FILE_TEMP_HEAD = getExternalCacheDir().getPath() + "/head.temp";//头像缓存
        ClientConstant.PATH_FILE_TEMP_CROPHEAD = getExternalCacheDir().getPath() + "/crop_head.temp";//截取头像缓存

        activities = new ArrayList<>();

        //数据库初始化
        DbManage.init(getApplicationContext(), new ZeroDbAdapter(ClientConstant.PATH_DB_NAME));
        //加载图片工具初始化
        ImageLoadUtil.init(getApplicationContext());

        Log.e(TAG, "----------------ApplicationEx Init End------------------");

    }

    public synchronized void closeAllActivity() {
        synchronized (activities) {
            Iterator<Activity> iter = activities.iterator();
            while (iter.hasNext()) {
                Activity activity = iter.next();
                iter.remove();
                if (activity != null && !activity.isFinishing()) {
                    activity.finish();
                }
            }
        }
    }

    private void handerException() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
//                WebServiceManage.self().saveExceptionLog(WebServiceManage.ClientType.MOBILE_CLIENT, sw.toString(), AndroidUtil.getPhoneInfo(ApplicationEx.this), new CommonCallback() {
//                    @Override
//                    public void callback(boolean isok, String msg) {
//                        if (!isok) {
//                            MLogUtil.d(msg);
//                        }
//                    }
//                });
                pw.close();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra("haveError", "exceptionReboot");
                        intent.setClass(ApplicationEx.this, WelcomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        ApplicationEx.this.startActivity(intent);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }).start();
            }
        });
    }

    public void exit() {
        for (Activity activity : activities) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
        //System.exit(0);
    }
}
