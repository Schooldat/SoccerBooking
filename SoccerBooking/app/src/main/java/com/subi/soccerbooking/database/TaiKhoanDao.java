package com.subi.soccerbooking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.subi.soccerbooking.model.User;

import java.util.ArrayList;

public class TaiKhoanDao {
    Database dtb;
    SQLiteDatabase db;

    public TaiKhoanDao(Context context) {
        dtb = new Database(context);
        db = dtb.getWritableDatabase();
    }

    public ArrayList<User> getAll() {
        ArrayList<User> list = new ArrayList<>();
        Cursor cs = db.rawQuery("SELECT * FROM taiKhoan", null);
        cs.moveToFirst();
        while (!cs.isAfterLast()) {
            String email = cs.getString(0);
            String pass = cs.getString(1);
            User taiKhoan = new User(email, pass);
            list.add(taiKhoan);
            cs.moveToNext();
        }
        cs.close();
        return list;
    }


    public boolean them(User taiKhoan) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", taiKhoan.getEmail());
        contentValues.put("pass", taiKhoan.getPassworld());
        long r = db.insert("taiKhoan", null, contentValues);
        if (r <= 0) {
            return false;
        }
        return true;
    }

    public boolean checkLogin(User taiKhoan) {
        String Query = "Select * from taiKhoan where " + "email" + " = " + "'" + taiKhoan.getEmail() + "'" + " AND pass" + " = " + "'" + taiKhoan.getPassworld() + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();

        return true;
    }



}
