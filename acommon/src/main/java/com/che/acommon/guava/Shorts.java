package com.che.acommon.guava;

/**
 * Created by LC on 2016/4/15.
 */
public final class Shorts {
    public static short fromBytes(byte b1, byte b2) {
        return (short) ((b1 << 8) | (b2 & 0xFF));
    }
}
