package com.che.acommon.util;

import android.util.Log;

import java.io.File;

/**
 * Created by Che on 2016/08/03.
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    /**
     * 创建文件夹
     *
     * @param file
     */
    public static void mkdirs(File file) {
        if (!file.exists()) {
            if (file.mkdirs()) {
                Log.e(TAG, "mkdirs: 文件夹创建成功");
            } else {
                Log.e(TAG, "mkdirs: 文件夹创建失败");
            }
        } else {
            Log.e(TAG, "mkdirs: 文件夹已存在");
        }
    }
}
