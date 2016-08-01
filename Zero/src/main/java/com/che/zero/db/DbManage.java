package com.che.zero.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.che.acommon.bean.BeanInfo;
import com.che.acommon.bean.BeanManage;
import com.che.acommon.bean.PropertyInfo;
import com.che.acommon.guava.Strings;
import com.che.acommon.util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unchecked")
public class DbManage {
    private final static String TAG = "common.DbManage";

    private static DbAdapter dbAdapter;
    private static DbManage instance;

    private static AtomicInteger mOpenCounter = new AtomicInteger();

    public static synchronized void init(Context context, DbAdapter dbAdapter) {
        if (instance == null) {
            DbManage.dbAdapter = dbAdapter;
            instance = new DbManage(context);
        }
    }

    // 主键
    private static final String KEY_NAME = "id";
    // 上下文
    private Context context;
    // 数据库Helper
    private DatabaseHelper DBHelper;
    // 数据库操作对象
    private SQLiteDatabase db;

    private DbManage(Context ctx) {
        context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    /**
     * 获取数据库管理器
     * <p/>
     * 上下文
     *
     * @return 数据库管理器
     */
    private synchronized static DbManage getDbManage() {
        Log.e(TAG, "获取一个数据库管理器");
        if (instance == null) {
            throw new IllegalStateException(DbManage.class.getSimpleName() + " is not initialized, call initialize(..) method first.");
        }
        if (mOpenCounter.incrementAndGet() == 1) {
            instance.db = instance.DBHelper.getWritableDatabase();
        }
        return instance;
    }

    /**
     * 关闭数据库
     */
    private synchronized void close() {
        if (mOpenCounter.decrementAndGet() == 0) {
            db.close();
            DBHelper.close();
        }
    }

    /**
     * 向数据库中插入数据bean
     */
    public static <T extends BaseEntity> int insert(@NonNull BaseEntity entity) {
        if (entity == null) {
            return 0;
        }
        Class<T> clazz = (Class<T>) entity.getClass();
        Table t = clazz.getAnnotation(Table.class);
        if (t == null) {
            return 0;
        }
        entity.setCreateDate(DateUtil.getNowDateTime());
        entity.setModifyDate(DateUtil.getNowDateTime());

        ContentValues cv = new ContentValues();
        BeanInfo info = BeanManage.self().getBeanInfo(clazz);
        PropertyInfo[] pis = info.getPropertyInfos();
        for (PropertyInfo pi : pis) {
            try {
                Object result = pi.getReadMethod().invoke(entity, new Object[0]);
                if (result != null) {
                    cv.put(pi.getName(), result.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, "数据库表插入：TableName:" + t.name() + " values:" + cv);
        DbManage dbManage = getDbManage();
        int r = -1;
        try {
            long rowid = dbManage.db.insert(t.name(), null, cv);
            Log.e(TAG, "数据库表插入：TableName:" + t.name() + " rowid:" + rowid);
            if (rowid > 0) {
                Cursor cursor = dbManage.db.rawQuery("select id from " + t.name() + " where rowid = " + rowid, null);
                cursor.moveToFirst();
                r = cursor.getInt(0);
            } else {
                r = -1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        dbManage.close();
        return r;
    }

    /**
     * 更新一个bean,如果id为null,则失败
     *
     * @param entity
     * @param where  修改条件，如"id=1"
     * @return boolean
     */
    public static <T extends BaseEntity> boolean update(@NonNull BaseEntity entity, @NonNull String where) {
        if (entity == null) {
            return false;
        }
        Class<T> clazz = (Class<T>) entity.getClass();
        Table t = clazz.getAnnotation(Table.class);
        if (t == null) {
            return false;
        }
        entity.setModifyDate(DateUtil.getNowDateTime());
        ContentValues cv = new ContentValues();
        BeanInfo info = BeanManage.self().getBeanInfo(clazz);
        PropertyInfo[] pis = info.getPropertyInfos();
        for (PropertyInfo pi : pis) {
            try {
                Object result = pi.getReadMethod().invoke(entity, new Object[0]);

                if (result != null) {
                    if (!pi.getName().equals("id") && !pi.getName().equals("createDate")) {
                        cv.put(pi.getName(), result.toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DbManage dbManage = getDbManage();
        Log.e(TAG, "数据库表更新：TableName:" + t.name() + " values:" + cv + " where " + where);
        boolean r = false;
        try {
            r = dbManage.db.update(t.name(), cv, where, null) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbManage.close();
        return r;

    }

    /**
     * 通用删除一个bean
     *
     * @param where 删除条件，如"id=1"
     * @return boolean
     */
    public static <T extends BaseEntity> boolean delete(@NonNull Class<T> clazz, @NonNull String where) {
        if (clazz == null) {
            return false;
        }
        Table t = clazz.getAnnotation(Table.class);
        if (t == null) {
            return false;
        }
        Log.e(TAG, "数据库表删除：TableName:" + t.name() + " where " + where);
        DbManage dbManage = getDbManage();
        boolean re = false;
        try {
            int r = dbManage.db.delete(t.name(), where, null);
            Log.e(TAG, "数据库表删除：TableName:" + t.name() + " 删除数量: " + r);
            re = r > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbManage.close();
        return re;
    }

    /**
     * 查询所有
     *
     * @param sql
     * @param selectionArgs
     * @return
     */
    public static List<Map<String, Object>> getList(@NonNull String sql, String[] selectionArgs) {
        // 查询
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        DbManage dbManage = getDbManage();
        try {
            Cursor cur = dbManage.db.rawQuery(sql, selectionArgs);
            while (cur.moveToNext()) {
                Map<String, Object> map = new HashMap<String, Object>();
                String[] names = cur.getColumnNames();
                for (String n : names) {
                    map.put(n, cur.getString(cur.getColumnIndex(n)));
                }
                list.add(map);
            }
            cur.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbManage.close();
        Log.e(TAG, "数据库表查询列表：getList:" + list);
        return list;
    }

    /**
     * 查询所有
     *
     * @return
     */
    public static <T> List<T> getList(@NonNull Class<T> clazz, String where) {
        // 查询
        BeanInfo info = BeanManage.self().getBeanInfo(clazz);
        if (info == null) {
            return null;
        }
        Table t = clazz.getAnnotation(Table.class);
        if (t == null) {
            return null;
        }

        PropertyInfo[] pis = info.getPropertyInfos();
        String sql = "select * from " + t.name();
        if (!Strings.isNullOrEmpty(where)) {
            sql = sql + " where " + where;
        }
        List<T> list = new ArrayList<T>();
        DbManage dbManage = getDbManage();
        try {
            Log.e(TAG, "sql:" + sql);
            Cursor cur = dbManage.db.rawQuery(sql, null);
            while (cur.moveToNext()) {
                Map<String, Object> map = new HashMap<String, Object>();
                String[] names = cur.getColumnNames();
                for (String n : names) {
                    map.put(n, cur.getString(cur.getColumnIndex(n)));
                }

                Object obj = clazz.newInstance();
                for (PropertyInfo pi : pis) {
                    String propertyName = pi.getName();
                    if (map.containsKey(propertyName)) {
                        Object value = map.get(propertyName);
                        pi.getWriteMethod().invoke(obj, getValueByType(pi.getField().getType(), value));
                    }
                }
                list.add((T) obj);
            }
            cur.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbManage.close();
        Log.e(TAG, "数据库表查询列表：getList:" + list);
        return list;
    }

    /**
     * 查询map对象
     *
     * @param sql
     * @param selectionArgs
     * @return
     */
    public static Map<String, Object> getMap(@NonNull String sql, String[] selectionArgs) {
        DbManage dbManage = getDbManage();
        try {
            Cursor cur = dbManage.db.rawQuery(sql, selectionArgs);
            if (cur.getCount() > 0) {
                cur.moveToFirst();
                Map<String, Object> map = new HashMap<String, Object>();
                String[] names = cur.getColumnNames();
                for (String n : names) {
                    map.put(n, cur.getString(cur.getColumnIndex(n)));
                }
                cur.close();
                return map;
            }
            cur.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbManage.close();
        return null;
    }

    /**
     * 查询条数
     *
     * @param sql
     * @param selectionArgs
     * @return
     */
    public static Integer getCount(@NonNull String sql, String[] selectionArgs) {
        DbManage dbManage = getDbManage();
        try {
            Cursor cur = dbManage.db.rawQuery("select count(*) as count_tmp from (" + sql + ") ", selectionArgs);
            if (cur.moveToFirst()) {
                Integer r = cur.getInt(0);
                cur.close();
                Log.e(TAG, "数据库查询条数： " + r);
                return r;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbManage.close();
        return 0;
    }

    public static void doSql(@NonNull String sql) {
        DbManage dbManage = getDbManage();
        try {
            dbManage.db.rawQuery(sql, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbManage.close();
    }

    /**
     * 分页查询
     *
     * @param pager (第几页，每页个数)
     *              表名称
     * @param
     * @return
     */
    public static Pager getPager(@NonNull Pager pager) {
        String selectsql = pager.getSelectSql();
        String[] selectionArgs = pager.getParam();
        pager.setTotalCount(getCount(selectsql, selectionArgs));
        String sql = selectsql + " order by " + pager.getSort() + " " + pager.getOrder() + " limit ?,?";
        if (selectionArgs == null) {
            selectionArgs = new String[0];
        }
        String[] param = new String[selectionArgs.length + 2];
        for (int i = 0; i < selectionArgs.length; i++) {
            param[i] = selectionArgs[i];
        }
        param[selectionArgs.length] = String.valueOf(pager.getStartItem());
        param[selectionArgs.length + 1] = String.valueOf(pager.getRows());
        pager.setList(getList(sql, param));
        return pager;
    }

    public static <T> T getBean(@NonNull Class<T> clazz, @NonNull String where) {
        if (clazz == null) {
            return null;
        }
        Table t = clazz.getAnnotation(Table.class);
        if (t == null) {
            return null;
        }
        String sql = "select * from " + t.name() + " where " + where;
        Log.e(TAG, sql);
        Map<String, Object> map = getMap(sql, null);
        if (map == null)
            return null;
        try {
            BeanInfo info = BeanManage.self().getBeanInfo(clazz);
            Object obj = clazz.newInstance();
            PropertyInfo[] pis = info.getPropertyInfos();
            for (PropertyInfo pi : pis) {
                String propertyName = pi.getName();
                if (map.containsKey(propertyName)) {
                    Object value = map.get(propertyName);
                    pi.getWriteMethod().invoke(obj, getValueByType(pi.getField().getType(), value));
                }
            }
            return (T) obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, dbAdapter.getDbName(), null, dbAdapter.getDbVersion());
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            dbAdapter.onCreate(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            dbAdapter.onUpgrade(db, oldVersion, newVersion);
        }
    }

    public static abstract class DbAdapter {
        public abstract String getDbName();

        public abstract int getDbVersion();

        public abstract Class<?>[] getBeanClass();

        public void onCreate(SQLiteDatabase db) {
            Class<?>[] clazzes = getBeanClass();
            for (Class<?> clazz : clazzes) {
                Table t = clazz.getAnnotation(Table.class);

                BeanInfo beanInfo = BeanManage.self().getBeanInfo(clazz);
                PropertyInfo[] pis = beanInfo.getPropertyInfos();

                Cursor tableInf = db.query("sqlite_master", new String[]{"sql"}, "type='table' and name='" + t.name() + "'", null, null, null, null);
                if (tableInf.getCount() > 0) {
                    tableInf.moveToFirst();
                    String csql = tableInf.getString(tableInf.getColumnIndex("sql"));
                    String[] tempRows = csql.substring(csql.indexOf("(") + 1, csql.indexOf(")")).replace("\n", "").replace("`", "").split(",");
                    for (int i = 0; i < pis.length; i++) {
                        boolean have = false;
                        PropertyInfo descriptor = pis[i];
                        Class<?> pt = descriptor.getField().getClass();
                        if (descriptor.getReadMethod() == null || descriptor.getWriteMethod() == null) {
                            continue;
                        }

                        for (String row : tempRows) {
                            if (row.trim().lastIndexOf("\t") > -1) {
                                if (row.trim().split("\t")[0].trim().equals(descriptor.getName())) {
                                    have = true;
                                    break;
                                }
                            } else {
                                if (row.trim().split(" ")[0].trim().equals(descriptor.getName())) {
                                    have = true;
                                    break;
                                }
                            }
                        }
                        if (!have) {
                            String addsql = descriptor.getName() + " " + DbManage.getDbType(pt);
                            if (addsql != null) {
                                db.execSQL("ALTER TABLE " + t.name() + " ADD COLUMN " + addsql);
                            }
                        }
                    }
                } else {
                    StringBuffer sql = new StringBuffer("CREATE TABLE IF NOT EXISTS " + t.name() + " (");

                    Log.e(TAG, sql + "");
                    for (int i = 0; i < pis.length; i++) {
                        PropertyInfo pi = pis[i];
                        if (DbManage.KEY_NAME.equals(pi.getName())) {
                            sql.append(DbManage.KEY_NAME + " integer primary key AUTOINCREMENT");
                        } else {
                            sql.append(pi.getName() + " " + DbManage.getDbType(pi.getField().getType()));
                        }
                        if (i == pis.length - 1) {
                            sql.append(")");
                        } else {
                            sql.append(",");
                        }
                    }
                    db.execSQL(sql.toString());
                }
                tableInf.close();
            }
        }

        public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }

    private static String getDbType(Class<?> clazz) {
        if (clazz == null) {
            return "TEXT";
        }
        if (clazz.equals(Integer.class) || clazz.equals(int.class) || clazz.equals(Long.class) || clazz.equals(long.class) || clazz.equals(Short.class) || clazz.equals(short.class)) {
            return "INTEGER";
        } else if (clazz.equals(Double.class) || clazz.equals(double.class) || clazz.equals(Float.class) || clazz.equals(float.class)) {
            return "REAL";
        } else {
            return "TEXT";
        }
    }

    private static Object getValueByType(Class<?> clazz, Object value) {
        if (clazz == null || value == null) {
            return null;
        }
        if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
            return Integer.parseInt(value + "");
        } else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
            return Double.parseDouble(value + "");
        } else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
            return Float.parseFloat(value + "");
        } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
            return Long.parseLong(value + "");
        } else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
            return Short.parseShort(value + "");
        } else if (clazz.equals(String.class)) {
            return String.valueOf(value);
        } else {
            return null;
        }

    }
}
