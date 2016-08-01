package com.che.zero.db;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

/**
 * Created by liuyixian on 16/1/10.
 */
public class ZeroDbAdapter extends DbManage.DbAdapter {
    private String dbName;

    @Override
    public String getDbName() {
        return dbName;
    }

    @Override
    public int getDbVersion() {
        return 1;
    }

    @Override
    public Class<?>[] getBeanClass() {
        return new Class<?>[]{
//                JsonBeanInfo.class,
//                AppVersionInfo.class,
//                HomePageAd.class,
//                UserMessage.class,
//                UserCheckInfo.class,
//                HealthDateSynInfo.class,
//                HealthHistoryData.class,
//                UserAccount.class,
//                HealthRemind.class
        };
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onCreate(db);
    }

    public ZeroDbAdapter(@NonNull String dbName) {
        this.dbName = dbName;
    }
}
