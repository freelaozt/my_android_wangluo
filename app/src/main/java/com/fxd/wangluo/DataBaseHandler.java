package com.fxd.wangluo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LaoZhang on 2017/4/23.
 */
public class DataBaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LocalDB";
    private static final String TABLE_NAME = "User_8";
    private static final int VERSION = 1;
    private static final String KEY_ID = "localId";
    private static final String KEY_USER_ID = "netUserId";
    private static final String KEY_HEAD_IMG = "netHeadImg";
    private static final String KEY_USERNAME = "netUserName";
    private static final String KEY_EMAIL = "netEmail";
    private static final String KEY_PASSWORD = "netPassWord";
    private static final String KEY_LABEL = "netLabel";

    //建表语句
    private static final String CREATE_TABLE = "create table "
            + TABLE_NAME + "(" + KEY_ID + " integer primary key,"
            + KEY_USER_ID + " text not null," + KEY_HEAD_IMG + " text not null,"
            + KEY_USERNAME + " text not null," + KEY_EMAIL + " text not null,"
            + KEY_PASSWORD + " text not null," + KEY_LABEL + " text not null);";

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * 1.获得全部数据
     * 2.获取单行数据
     * 3.插入数据
     * 4.删除数据
     */
    //查表所有数据是否有数据
    public List<UserBean> checkUserTable() {
        List<UserBean> list = new ArrayList<UserBean>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                UserBean bean = new UserBean();
                bean.setLocalId(Integer.parseInt(c.getString(0)));
                bean.setNetUserId(Integer.parseInt(c.getString(1)));
                bean.setNetHeadImg(c.getString(2));
                bean.setNetUserName(c.getString(3));
                bean.setNetEmail(c.getString(4));
                bean.setNetPassWord(c.getString(5));
                bean.setNetLabel(c.getString(6));
                list.add(bean);
            } while (c.moveToNext());
        } else {
            list = null;
        }
        return list;
    }

    //查询全部数据
    public UserBean queryData() {
        SQLiteDatabase db = this.getWritableDatabase();

        //Cursor对象返回查询结果
        Cursor c = db.rawQuery("select * from " + TABLE_NAME, null);

        UserBean userBean = null;
        //注意返回结果有可能为空
        if (c.moveToFirst()) {
//   查询的数据放入Bean中
            userBean = new UserBean(c.getInt(0), c.getInt(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6));
        }
        c.close();
        db.close();
        return userBean;
    }

    //添加登录数据
    public void addUserData(UserBean bean) {
        String insertSql = "insert into " + TABLE_NAME + " (" +
                KEY_USER_ID + "," +
                KEY_HEAD_IMG + "," +
                KEY_USERNAME + "," +
                KEY_EMAIL + "," +
                KEY_PASSWORD + "," +
                KEY_LABEL + ")" + " values" + "(" +
                "'" + bean.getNetUserId() + "'," +
                "'" + bean.getNetHeadImg() + "'," +
                "'" + bean.getNetUserName() + "'," +
                "'" + bean.getNetEmail() + "'," +
                "'" + bean.getNetPassWord() + "'," +
                "'" + bean.getNetLabel() + "'" +
                ")";
        getWritableDatabase().execSQL(insertSql);
    }

    //全删除
    public void deleteData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

}
