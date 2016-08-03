package com.che.acommon.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.che.acommon.guava.Strings;

/**
 * Created by Che on 2016/08/03.
 */
public class SharedPreferencesUtil {
    public final static String XML_Settings = "sharedPreferences";// 生成SharedPreferences文件名
    private static SharedPreferences sharedPreferences;

    /**
     * 保存String键值对
     *
     * @param context 上下文
     * @param key     键
     * @param value   值
     */
    public static void saveSharedPreString(Context context, String key, String value) {
        sharedPreferences = context.getSharedPreferences(XML_Settings, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).commit();
    }

    /**
     * 获取String键值对
     *
     * @param context 上下文
     * @param key     键
     */
    public static String getSharedPreString(Context context, String key) {
        sharedPreferences = context.getSharedPreferences(XML_Settings, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    /**
     * 获取String键值对
     *
     * @param context 上下文
     * @param key     键
     */
    public static String getSharedPreString(Context context, String key, String defaultValue) {
        sharedPreferences = context.getSharedPreferences(XML_Settings, Context.MODE_PRIVATE);
        if (Strings.isNullOrEmpty(defaultValue)) {
            return sharedPreferences.getString(key, "");
        }
        return sharedPreferences.getString(key, defaultValue);
    }


    /**
     * 保存boolean键值对
     *
     * @param context 上下文
     * @param key     键
     * @param value   值
     */
    public static void saveSharedPreBoolean(Context context, String key, boolean value) {
        sharedPreferences = context.getSharedPreferences(XML_Settings, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    /**
     * 获取boolean键值对
     *
     * @param context 上下文
     * @param key     键
     * @return
     */
    public static boolean getSharedPreBoolean(Context context, String key) {
        sharedPreferences = context.getSharedPreferences(XML_Settings, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * 获取boolean键值对
     *
     * @param context 上下文
     * @param key     键
     * @return
     */
    public static boolean getSharedPreBoolean(Context context, String key, boolean defaultValue) {
        sharedPreferences = context.getSharedPreferences(XML_Settings, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }


    /**
     * 保存int类型的数据到SharedPreference配置文件
     *
     * @param context 上下文
     * @param key     键
     * @param value   值
     */
    public static void saveSharedPreInteger(Context context, String key, int value) {
        sharedPreferences = context.getSharedPreferences(XML_Settings, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key, value).commit();
    }

    /**
     * 从SharedPreference配置文件中获取int类型的值
     *
     * @param context 上下文
     * @param key     键
     * @return 返回int类型的value值
     */
    public static int getSharedPreInteger(Context context, String key, int defaultValue) {
        sharedPreferences = context.getSharedPreferences(XML_Settings, Context.MODE_PRIVATE);

        return sharedPreferences.getInt(key, defaultValue);
    }

    /**
     * 保存Long类型的数据到SharedPreference配置文件
     *
     * @param context 上下文
     * @param key     键
     * @param value   值
     */
    public static void saveSharedPreLong(Context context, String key, Long value) {
        sharedPreferences = context.getSharedPreferences(XML_Settings, Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(key, value).commit();
    }

    /**
     * 从SharedPreference配置文件中获取int类型的值
     *
     * @param context 上下文
     * @param key     键
     * @return 返回int类型的value值
     */
    public static long getSharedPreLong(Context context, String key, long defaultValue) {
        sharedPreferences = context.getSharedPreferences(XML_Settings, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, defaultValue);

    }

    /**
     * 清空SharedPreference中的所有String类型的数值
     *
     * @param context 上下文
     */
    public static void clearSave(Context context) {
        sharedPreferences = context.getSharedPreferences(XML_Settings, Context.MODE_PRIVATE);
        for (String name : sharedPreferences.getAll().keySet()) {
            saveSharedPreString(context, name, "");
        }
    }
}
