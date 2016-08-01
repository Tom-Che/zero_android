package com.che.zero.test;

/**
 * Created by Che on 2016/08/01.
 */

/**
 * 单例类
 */
public class Singleton {
    private static Singleton singleton;

    public static Singleton self() {
        synchronized (Singleton.class) {
            if (singleton == null) {
                singleton = new Singleton();
            }
        }
        return singleton;
    }
//    public synchronized static Singleton self() {
//        if (singleton == null) {
//            singleton = new Singleton();
//        }
//        return singleton;
//    }
}
