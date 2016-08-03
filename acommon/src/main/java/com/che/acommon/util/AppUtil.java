package com.che.acommon.util;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by Che on 2016/08/03.
 */
public class AppUtil {

    /**
     * 获取当前上下文进程名称
     *
     * @param context
     * @return
     */
    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
