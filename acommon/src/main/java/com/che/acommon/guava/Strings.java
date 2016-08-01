package com.che.acommon.guava;

import android.support.annotation.Nullable;

/**
 * Created by LC on 2016/4/15.
 */
public final class Strings {
    public static boolean isNullOrEmpty(@Nullable String string) {
        return string == null || string.length() == 0; // string.isEmpty() in Java 6
    }
}
