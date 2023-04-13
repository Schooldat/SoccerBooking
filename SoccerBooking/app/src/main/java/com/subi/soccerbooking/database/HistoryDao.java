package com.subi.soccerbooking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.subi.soccerbooking.HistoryActivity;
import com.subi.soccerbooking.model.History;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HistoryDao {
    Database dtb;
    SQLiteDatabase db;
    private DecimalFormat fm = new DecimalFormat("#,###");
    //kiểu format số, 1 # đại diện cho 1 con số

    public HistoryDao(Context context) {
        dtb = new Database(context);
        db = dtb.getWritableDatabase();
    }
// lấy hết những cái lịch sữ bằng cái email
    public ArrayList<History> getAllByEmail(String emailx) {
        int sum_all=0;

        ArrayList<History> list = new ArrayList<>();
        Cursor cs = db.rawQuery("SELECT * FROM history WHERE email = "+"'"+emailx+"'", null);
        cs.moveToFirst();
        while (!cs.isAfterLast()) {
            int id = cs.getInt(0);
            String name = cs.getString(1);
            String price = cs.getString(2);
            String hour = cs.getString(3);
            String date = cs.getString(4);
            String email = cs.getString(5);
            String nameuser = cs.getString(6);
            String phone = cs.getString(7);
            sum_all+=(Integer.parseInt(price)*Integer.parseInt(hour));
            History history = new History(id, name, price, hour,date, email,nameuser,phone);
            list.add(history);
            cs.moveToNext();
        }
        HistoryActivity.sum.setText(fm.format(sum_all)+" VNĐ");
        cs.close();
        return list;
    }
    // tìm ls giao dịch dựa trên ngày và email
    public ArrayList<History> getAllByEmailandDate(String emailx,String datex) {
        int sum_all=0;
        ArrayList<History> list = new ArrayList<>();
        Cursor cs = db.rawQuery("SELECT * FROM history WHERE email = "+"'"+emailx+"' AND date = "+"'"+datex+"'", null);
        cs.moveToFirst();
        // đưa con trỏ lên đầu để duyệt bảng
        while (!cs.isAfterLast()) {
            // nếu con trỏ vẫn chưa đến cuối bảng
            int id = cs.getInt(0);
            String name = cs.getString(1);
            String price = cs.getString(2);
            String hour = cs.getString(3);
            String date = cs.getString(4);
            String email = cs.getString(5);
            String nameuser = cs.getString(6);
            String phone = cs.getString(7);
            sum_all+=(Integer.parseInt(price)*Integer.parseInt(hour));
            History history = new History(id, name, price, hour,date, email,nameuser,phone);
            list.add(history);
            cs.moveToNext();
        }
        //lấy từ activity và set vào danh thu
        HistoryActivity.sum.setText(fm.format(sum_all)+" VNĐ");
        cs.close();
        return list;
    }

// Thanh toán
    public boolean them(History history) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", history.getName());
        contentValues.put("price", history.getPrice());
        contentValues.put("hour", history.getHour());
        contentValues.put("date", history.getDate());
        contentValues.put("email", history.getEmail());
        contentValues.put("nameuser", history.getNameuser());
        contentValues.put("phone", history.getPhone());
        //db.insert trả về kdl không phải số nguyên
        long r = db.insert("history", null, contentValues);
        if (r <= 0) {
            return false;
        }
        return true;
    }




}
