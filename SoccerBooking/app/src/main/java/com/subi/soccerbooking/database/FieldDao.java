package com.subi.soccerbooking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.subi.soccerbooking.model.Field;

import java.util.ArrayList;

public class FieldDao {
    Database dtb;
    SQLiteDatabase db;

    public FieldDao(Context context) {
        dtb = new Database(context);
        db = dtb.getWritableDatabase();
    }

    public ArrayList<Field> getAllByEmail(String emailx) {
        ArrayList<Field> list = new ArrayList<>();
        Cursor cs = db.rawQuery("SELECT * FROM field WHERE email = "+"'"+emailx+"'", null);
        cs.moveToFirst();
        while (!cs.isAfterLast()) {
            int id = cs.getInt(0);
            String name = cs.getString(1);
            String price = cs.getString(2);
            String image = cs.getString(3);
            String email = cs.getString(4);
            Field field = new Field(id, name, price, image, email);
            list.add(field);
            cs.moveToNext();
        }
        cs.close();
        return list;
    }


    public boolean them(Field field) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", field.getName());
        contentValues.put("price", field.getPrice());
        contentValues.put("image", field.getImage());
        contentValues.put("email", field.getEmail());
        long r = db.insert("field", null, contentValues);
        if (r <= 0) {
            return false;
        }
        return true;
    }


    public boolean sua(Field field) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", field.getName());
        contentValues.put("price", field.getPrice());
        contentValues.put("image", field.getImage());
        contentValues.put("email", field.getEmail());
        int r = db.update("field", contentValues, "id=?", new String[]{String.valueOf(field.getId())});
        if (r <= 0) {
            return false;
        }
        return true;
    }

    public boolean xoa(Field field) {
        int r = db.delete("field", "id=?", new String[]{String.valueOf(field.getId())});
        if (r <= 0) {
            return false;
        }
        return true;
    }
}
