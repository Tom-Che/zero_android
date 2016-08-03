package com.che.zero.db;

import android.util.Log;

import com.che.acommon.guava.Strings;
import com.che.acommon.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class QueryUtil {
    private static final String TAG = "QueryUtil";
    /**
     * 包含
     */
    public final static int LIKE = 0;
    /**
     * 等于
     */
    public final static int EQUAL = 1;
    /**
     * 左边无法确定
     */
    public final static int LLIKE = 2;
    /**
     * 右边无法确定
     */
    public final static int RLIKE = 3;
    /**
     * 大于
     */
    public final static int GT = 4;
    /**
     * 小于
     */
    public final static int LT = 5;
    /**
     * 大于=
     */
    public final static int GTE = 9;
    /**
     * 小于=
     */
    public final static int LTE = 10;
    /**
     * 不包含
     */
    public final static int NOINCLUDE = 6;

    /**
     * 没有标签
     */
    public final static int NOTYPE = 7;

    public final static int NEQUAL = 8;

    public static final int NEW = 0;
    public static final int OLD = 1;

    private int type;
    private StringBuffer querySql = new StringBuffer("");
    private List<Object> params = new ArrayList<Object>();
    private Map<String, Object> map;

    public QueryUtil(Map<String, Object> map, int type) {
        this.map = map;
        this.type = type;
        if (this.type == 0) {
            querySql = new StringBuffer(" where 1=1 ");
        } else {
            querySql = new StringBuffer("");
        }
        Log.e(TAG, "查询数据:" + map);
    }

    public void clear() {
        params.clear();
        if (this.type == 0) {
            querySql = new StringBuffer(" where 1=1 ");
        } else {
            querySql = new StringBuffer("");
        }
    }

    public void setInParam(String sql, String[] values) {
        if (values.length > 500 || values == null) {
            return;
        }
        querySql.append(" and ").append(sql).append(" in(");
        int i = 0;
        for (String s : values) {
            if (i == values.length - 1) {
                querySql.append("?)");
            } else {
                querySql.append("?,");
            }
            params.add(s);
            i++;
        }
    }

    public void setParam(String sql, String key, int type, String defaultvalue) {
        String temp = getValueFromMap(map, key);
        if (!Strings.isNullOrEmpty(temp)) {
            temp = defaultvalue;
        }
        Log.e(TAG, "查询数据,key:" + key + ",value:" + temp);
        if (!Strings.isNullOrEmpty(temp)) {
            switch (type) {
                case QueryUtil.LIKE:
                    params.add("%" + temp.toString().trim() + "%");
                    sql += " like ?";
                    break;
                case QueryUtil.EQUAL:
                    params.add(temp.toString().trim());
                    sql += " = ?";
                    break;
                case QueryUtil.NEQUAL:
                    params.add(temp.toString().trim());
                    sql += " != ?";
                    break;
                case QueryUtil.LLIKE:
                    params.add("%" + temp.toString().trim());
                    sql += " like ?";
                    break;
                case QueryUtil.RLIKE:
                    params.add(temp.toString().trim() + "%");
                    sql += " like ?";
                    break;
                case QueryUtil.GT:
                    params.add(temp.toString().trim());
                    sql += " > ?";
                    break;
                case QueryUtil.LT:
                    params.add(temp.toString().trim());
                    sql += " < ?";
                    break;
                case QueryUtil.GTE:
                    params.add(temp.toString().trim());
                    sql += " >= ?";
                    break;
                case QueryUtil.LTE:
                    params.add(temp.toString().trim());
                    sql += " <= ?";
                    break;
                case QueryUtil.NOINCLUDE:
                    params.add("%" + temp.toString().trim() + "%");
                    sql += " not like ?";
                    break;
                case QueryUtil.NOTYPE:
                    params.add(temp.toString().trim());
                    break;
                default:
                    return;
            }
            querySql.append(" and ").append(sql);
        }
    }

    public void setParams(String sql, String[] keys, int[] types) {
        if (keys != null && keys.length > 0) {
            querySql.append(" and (").append(sql).append(")");
            for (String key : keys) {
                switch (type) {
                    case QueryUtil.LIKE:
                        params.add("%" + getValueFromMap(map, key) + "%");
                        sql += " like ?";
                        break;
                    case QueryUtil.EQUAL:
                        params.add(getValueFromMap(map, key));
                        sql += " = ?";
                        break;
                    case QueryUtil.NEQUAL:
                        params.add(getValueFromMap(map, key));
                        sql += " != ?";
                        break;
                    case QueryUtil.LLIKE:
                        params.add("%" + getValueFromMap(map, key));
                        sql += " like ?";
                        break;
                    case QueryUtil.RLIKE:
                        params.add(getValueFromMap(map, key) + "%");
                        sql += " like ?";
                        break;
                    case QueryUtil.GT:
                        params.add(getValueFromMap(map, key));
                        sql += " > ?";
                        break;
                    case QueryUtil.LT:
                        params.add(getValueFromMap(map, key));
                        sql += " < ?";
                        break;
                    case QueryUtil.NOINCLUDE:
                        params.add("%" + getValueFromMap(map, key) + "%");
                        sql += " not like ?";
                        break;
                    case QueryUtil.NOTYPE:
                        params.add(getValueFromMap(map, key));
                        break;
                    default:
                        return;
                }
            }
        }
    }

    public String getQuerySql() {
        return querySql.toString();
    }

    public Object[] getParams() {
        return params.toArray();
    }

    public static String getValueFromMap(Map<String, Object> map, String key) {
        if (Strings.isNullOrEmpty(key) || map == null) {
            return null;
        }
        Object value = map.get(key);
        String temp = null;
        if (value != null && value instanceof Object[]) {
            Object[] array = (Object[]) value;
            if (!Strings.isNullOrEmpty(array[0].toString().trim())) {
                temp = StringUtil.obj2Str(array[0]);
            }

        } else if (value != null && value instanceof Object) {
            if (!Strings.isNullOrEmpty(value.toString().trim())) {
                temp = StringUtil.obj2Str(value);
            }
        }
        return temp;
    }
}
