package com.che.acommon.util;

import java.util.Locale;

/**
 * Created by Che on 2016/07/25.
 */
public class StringUtil {

    /**
     * 字符串首字母大写
     *
     * @param temp
     * @return
     */
    public final static String convertFristToUpperCase(String temp) {
        String frist = temp.substring(0, 1);
        String other = temp.substring(1);
        return frist.toUpperCase(Locale.getDefault()) + other;
    }

    public static String obj2Str(Object obj) {
        if (obj != null) {
            return String.valueOf(obj);
        } else {
            return null;
        }
    }
}
