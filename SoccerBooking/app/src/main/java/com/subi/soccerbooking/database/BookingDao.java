package com.subi.soccerbooking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.subi.soccerbooking.model.Booking;

import java.util.ArrayList;

public class BookingDao {
    Database dtb;
    SQLiteDatabase db;

    public BookingDao(Context context) {
        dtb = new Database(context);
        db = dtb.getWritableDatabase();
    }

    public ArrayList<Booking> getAllByEmail(String idFieldx,String emailx) {
        ArrayList<Booking> list = new ArrayList<>();
        Cursor cs = db.rawQuery("SELECT * FROM booking WHERE idField = " + "'" + idFieldx + "'"+"AND email = "+"'"+emailx+"'", null);
        cs.moveToFirst();
        while (!cs.isAfterLast()) {
            int id = cs.getInt(0);
            String name = cs.getString(1);
            String phone = cs.getString(2);
            String datetime = cs.getString(3);
            String idField = cs.getString(4);
            String note = cs.getString(5);
            String email = cs.getString(6);
            Booking booking = new Booking(id, name, phone, datetime, idField, note, email);
            list.add(booking);
            cs.moveToNext();
        }
        cs.close();
        return list;
    }


    public boolean them(Booking booking) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", booking.getName());
        contentValues.put("phone", booking.getPhone());
        contentValues.put("datetime", booking.getDatetime());
        contentValues.put("idField", booking.getIdField());
        contentValues.put("note", booking.getNote());
        contentValues.put("email", booking.getEmail());
        long r = db.insert("booking", null, contentValues);
        if (r <= 0) {
            return false;
        }
        return true;
    }


    public boolean sua(Booking booking) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", booking.getName());
        contentValues.put("phone", booking.getPhone());
        contentValues.put("datetime", booking.getDatetime());
        contentValues.put("idField", booking.getIdField());
        contentValues.put("note", booking.getNote());
        contentValues.put("email", booking.getEmail());
        int r = db.update("booking", contentValues, "id=?", new String[]{String.valueOf(booking.getId())});
        if (r <= 0) {
            return false;
        }
        return true;
    }

    public boolean xoa(Booking booking) {
        int r = db.delete("booking", "id=?", new String[]{String.valueOf(booking.getId())});
        if (r <= 0) {
            return false;
        }
        return true;
    }
}
