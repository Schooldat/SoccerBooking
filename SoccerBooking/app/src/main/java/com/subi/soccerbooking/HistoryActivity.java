package com.subi.soccerbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.subi.soccerbooking.adapter.BookingAdapter;
import com.subi.soccerbooking.adapter.HistoryAdapter;
import com.subi.soccerbooking.adapter.ListFieldAdapter;
import com.subi.soccerbooking.database.BookingDao;
import com.subi.soccerbooking.database.HistoryDao;
import com.subi.soccerbooking.model.Booking;
import com.subi.soccerbooking.model.History;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HistoryActivity extends AppCompatActivity {
    private TextInputEditText from, to;
    private Button filter;
    public static TextView sum;
    private DecimalFormat fm = new DecimalFormat("#,###");
    private HistoryAdapter adapter;
    private ArrayList<History> list = new ArrayList<>();
    private HistoryDao historyDao;
    private RecyclerView recyclerView;
    public static int sum_all=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        init();
        sum_all=0;
        //Getall list
        list.addAll(historyDao.getAllByEmail(MainActivity.email));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //Set list vào adapter
        adapter = new HistoryAdapter(this, list);
        recyclerView.setAdapter(adapter);
        //chọn ngày
        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int d = calendar.get(Calendar.DAY_OF_MONTH);
                int m = calendar.get(Calendar.MONTH);
                int y = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        final String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        from.setText(date);
                    }
                }, y, m, d);
                datePickerDialog.show();
            }
        });

        //chọn ngày
        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int d = calendar.get(Calendar.DAY_OF_MONTH);
                int m = calendar.get(Calendar.MONTH);
                int y = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        final String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        to.setText(date);
                    }
                }, y, m, d);
                datePickerDialog.show();
            }
        });

        //Khi bấm lọc
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fromDate =  from.getText().toString();
                String toDate =  to.getText().toString();
                if (fromDate.equals(toDate)){
                    setTitle("Doanh thu ngày "+fromDate);
                    list.clear();
                    list.addAll(historyDao.getAllByEmailandDate(MainActivity.email, fromDate));
                    adapter.notifyDataSetChanged();
                }
                else {
                    list.clear();
                    int sum_all = 0;
                    setTitle(fromDate+" - "+toDate);
                    long from = Date.parse(fromDate);
                    long to = Date.parse(toDate);
                    Log.d("HISTORYLOG", from+" // "+to);
                    ArrayList<History> listCopy = new ArrayList<>();
                    listCopy.addAll(historyDao.getAllByEmail(MainActivity.email));
                    for(History history: listCopy){
                        Log.d("HISTORYLOG", "history: " +history.toString());
                        long date = Date.parse(history.getDate());
                        Log.d("HISTORYLOG", "Date: " +date);
                        if (date>=from&&date<=to){
                            list.add(history);
                            sum_all+=Integer.parseInt(history.getPrice())*Integer.parseInt(history.getHour());
                        }
                    }
                    sum.setText(fm.format(sum_all)+" VNĐ");
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void init() {
        setTitle("Doanh thu");
        from = findViewById(R.id.edt_from);
        to = findViewById(R.id.edt_to);
        filter = findViewById(R.id.btn_filter);
        sum = findViewById(R.id.tv_sum);
        historyDao = new HistoryDao(this);
        recyclerView = findViewById(R.id.rcv_history);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        menu.findItem(R.id.logout).setVisible(true);
        menu.findItem(R.id.history).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                //xóa hết dữ liệu đã lưu tk, mk
                SharedPreferences settings = getSharedPreferences("account", Context.MODE_PRIVATE);
                settings.edit().clear().apply();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.history:
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}