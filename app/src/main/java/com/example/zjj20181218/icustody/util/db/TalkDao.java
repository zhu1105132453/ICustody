package com.example.zjj20181218.icustody.util.db;

import android.content.Context;

import com.example.zjj20181218.icustody.javaBean.Talk;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

import java.sql.SQLException;
import java.util.List;

public class TalkDao {

    private Context context;
    // ORMLite提供的DAO类对象，第一个泛型是要操作的数据表映射成的实体类；第二个泛型是这个实体类中ID的数据类型
    private Dao<Talk, Integer> dao;

    public TalkDao(Context context) {
        this.context = context;

        try {
            this.dao = DatabaseHelper.getInstance(context).getDao(Talk.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 向talk表中添加一条数据
    public void insert(Talk data) {
        try {
            dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 删除talk表中的一条数据
    public void delete(Talk data) {
        try {
            dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //清空表数据
    public void clearTableData(){
        long count = 0;
        try {
            count = dao.countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (count != 0){
            DeleteBuilder deleteBuilder = dao.deleteBuilder();
            try {
                deleteBuilder.delete();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 修改talk表中的一条数据
    public void update(Talk data) {
        try {
            dao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询talk表中的所有数据
    public List<Talk> selectAll() {
        List<Talk> talks = null;
        try {
            talks = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return talks;
    }

    // 根据表列名取出所有信息
    public List<Talk> queryByUserId(String columnName, String value) {
        List<Talk> talk = null;
        QueryBuilder queryBuilder = dao.queryBuilder();
        try {
            //特殊字符处理
            SelectArg selectArg = new SelectArg(value);
            queryBuilder.where().eq(columnName, selectArg);
            talk = queryBuilder.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return talk;
    }
}
