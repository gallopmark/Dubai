package com.uroad.dubai.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.uroad.dubai.model.UserMDL;

import java.util.ArrayList;

public class UserDAL {

    Context context;
    DatabaseHelper mDbHelper = null;
    SQLiteDatabase mDb = null;

    public UserDAL(Context c) {
        context = c;
        mDbHelper = DatabaseHelper.getInstance(c);
        mDb = mDbHelper.getReadableDatabase();
    }

    public boolean insert(String userid,String name,String nickname,String sex,String avatar,
                          String type,String age){
        try {
            String sql = "insert into user_information(userid,name,nickname,sex,avatar,type,age) values(?,?,?,?,?,?,?)";
            mDb.execSQL(sql, new Object[]{userid,name,nickname,sex,avatar,type,age});
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean deleteAll() {
        try {
            String sql = "delete from user_information";
            mDb.execSQL(sql);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteUser(String id) {
        try {
            String sql = "delete from user_information where _id="+id;
            mDb.execSQL(sql);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ArrayList<UserMDL> getAllUser(){
        try{
            synchronized(UserDAL.this){
                String sql = "select * from user_information";
                Cursor cursor = mDb.rawQuery(sql, null);
                UserMDL mdl = null;
                ArrayList<UserMDL> datas = new ArrayList<>();
                while (cursor.moveToNext()) {
                    mdl = convert(cursor);
                    datas.add(mdl);
                }
                cursor.close();
                return datas;
            }
        }catch (Exception e){
            return new ArrayList<UserMDL>();
        }
    }

    private UserMDL convert(Cursor cursor){
        UserMDL mdl = new UserMDL();
        try{
            mdl.setUseruuid(cursor.getString(0));
            mdl.setName(cursor.getString(1));
            mdl.setNickname(cursor.getString(2));
            mdl.setSex(cursor.getString(3));
            mdl.setHeadimg(cursor.getString(4));
            mdl.setUserstatus(cursor.getString(5));
            mdl.setAge(cursor.getString(6));
        }catch (Exception e){ }
        return mdl;
    }

}
