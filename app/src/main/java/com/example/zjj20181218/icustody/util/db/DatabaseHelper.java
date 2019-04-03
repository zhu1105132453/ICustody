package com.example.zjj20181218.icustody.util.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.zjj20181218.icustody.javaBean.Talk;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库操作管理工具类
 *
 * 我们需要自定义一个类继承自ORMlite提供的OrmLiteSqliteOpenHelper，创建一个构造方法，重写两个方法onCreate()和onUpgrade()
 * 在onCreate()方法中使用TableUtils类中的createTable()方法初始化数据表
 * 在onUpgrade()方法中可以先删除所有表，然后调用onCreate()方法中的代码重新创建表
 *
 * 需要对这个类进行单例，保证整个APP中只有一个SQLite Connection对象
 *
 * 这个类通过一个Map集合来管理APP中所有的DAO，只有当第一次调用这个DAO类时才会创建这个对象（并存入Map集合中）
 * 其他时候都是直接根据实体类的路径从Map集合中取出DAO对象直接调用
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // 数据库名称
    public static final String DATABASE_NAME = "talk.db";

    // 本类的单例实例
    private static DatabaseHelper instance;

    // 存储APP中所有的DAO对象的Map集合
    private Map<String, Dao> daos = new HashMap<>();

    // 获取本类单例对象的方法
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper(context, DATABASE_NAME, 1);
                }
            }
        }
        return instance;
    }

    // 根据传入的DAO的路径获取到这个DAO的单例对象（要么从daos这个Map中获取，要么新创建一个并存入daos）
    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();
        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }

    /**
     * 构造方法，参数说明：
     * @param context 上下文
     * @param databaseName 数据库名
     * @param databaseVersion 数据库版本，当数据库版本升高时，会调用onUpgrade()方法
     */
    public DatabaseHelper(Context context, String databaseName, int databaseVersion) {
        super(context, databaseName, null, databaseVersion);
    }

    /**
     * 在该方法进行数据库创建表操作
     * @param database 表
     * @param connectionSource 连接源
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Talk.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *  在该方法进行数据库更新表操作
     * @param database 表
     * @param connectionSource 连接源
     * @param oldVersion 老版
     * @param newVersion 新版
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Talk.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 释放资源
    @Override
    public void close() {
        super.close();
        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }
}
