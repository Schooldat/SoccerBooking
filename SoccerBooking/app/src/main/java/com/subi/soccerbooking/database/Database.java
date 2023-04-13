package com.subi.soccerbooking.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    public Database(Context context) {
        super(context, "SOCCER_BOOKING", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Tạo bảng tài khoản
        String sql = "CREATE TABLE taiKhoan(" +
                "email text PRIMARY KEY, " +
                "pass text)";
        db.execSQL(sql);

        //Tạo bảng sân bóng
        sql = "CREATE TABLE field(" +
                "id integer PRIMARY KEY AUTOINCREMENT," +
                "name text, " +
                "price text, " +
                "image text, " +
                "email text )";
        db.execSQL(sql);

        //Thêm bảng lịch đặt sân
        sql = "CREATE TABLE booking(" +
                "id integer PRIMARY KEY AUTOINCREMENT," +
                "name text," +
                "phone text," +
                "datetime text," +
                "idField text references field(id)," +
                "note text," +
                "email text)";
        db.execSQL(sql);

        //Thêm bảng lịch sử
        //Thêm bảng lịch đặt sân
        sql = "CREATE TABLE history(" +
                "id integer PRIMARY KEY AUTOINCREMENT," +
                "name text," +
                "price text," +
                "hour text," +
                "date text," +
                "email text," +
                "nameuser text," +
                "phone text)";
        db.execSQL(sql);
    }


    // cập nhật DB, so sánh với 2 version, int vs inti1
    //để thay đổi DB khi version thay đổi

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
