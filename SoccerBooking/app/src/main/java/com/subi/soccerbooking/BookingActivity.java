package com.subi.soccerbooking;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.subi.soccerbooking.adapter.BookingAdapter;
import com.subi.soccerbooking.adapter.ShowDialog;
import com.subi.soccerbooking.database.BookingDao;
import com.subi.soccerbooking.database.HistoryDao;
import com.subi.soccerbooking.model.Booking;
import com.subi.soccerbooking.model.Field;
import com.subi.soccerbooking.model.History;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {
    private String phone = "";
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 111;
    private static final int REQUEST_CALL = 1;
    private Field field;
    private String TAG = "BookingActivityLOG";
    private SwipeMenuListView swipeMenuListView;
    private BookingAdapter adapter;
    private ArrayList<Booking> list = new ArrayList<>();
    private BookingDao bookingDao;
    private FloatingActionButton add;
    private ShowDialog showDialog;
    private DatePickerDialog datePickerDialog;
    private String pattern = "dd/MM/yyyy";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    private DecimalFormat fm = new DecimalFormat("#,###");
    private HistoryDao historyDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);


        //Lấy idField từ MainActivity
        try {
            //nhận dl từ main field
            field = (Field) getIntent().getSerializableExtra("field");
            Log.d(TAG, field.toString());
            //Set tiêu đề
            setTitle("Lịch " + field.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        init();

        //Get list booking
        list.clear();
        list.addAll(bookingDao.getAllByEmail(String.valueOf(field.getId()), MainActivity.email));
        Log.d(TAG, "Size list: " + list.size());
        for (Booking x : list) {
            Log.d(TAG, "add: " + x.toString());
            Log.d(TAG, "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        }
        //set adapter
        adapter = new BookingAdapter(BookingActivity.this, list, field);
        swipeMenuListView.setAdapter(adapter);

        //Khi click item
        swipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Booking booking = list.get(i);
                Dialog dialog = new Dialog(BookingActivity.this);
                dialog.setContentView(R.layout.add_pay);
                dialog.setCancelable(true);
                Window window = dialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (dialog != null && dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                //Khai báo biến
                TextInputEditText name = dialog.findViewById(R.id.edt_name);
                TextInputEditText price = dialog.findViewById(R.id.edt_price);
                TextInputEditText hour = dialog.findViewById(R.id.edt_hour);

                //Set lên
                name.setText(booking.getName());
                price.setText(fm.format(Integer.parseInt(field.getPrice())) + " VNĐ");

                Button add = dialog.findViewById(R.id.btnThem);
                Button cancle = dialog.findViewById(R.id.btnHuy);

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String time = hour.getText().toString();
                        if (time.isEmpty()) {
                            showDialog.show("Không được để trống");
                        } else {
                            History history = new History(0, field.getName(), field.getPrice(), time, booking.getDatetime().split("\\s")[0], MainActivity.email,booking.getName(), booking.getPhone());
                            if (historyDao.them(history)) {
                                //THanh toán
                                Log.d(TAG, history.toString());
                                showDialog.show("Số tiền cần thanh toán:\n" + (fm.format(Integer.parseInt(field.getPrice()) * Integer.parseInt(time))) + " VNĐ");
                                dialog.dismiss();
                            } else {
                                showDialog.show("Thanh toán thất bại!");
                                dialog.dismiss();
                            }
                        }
                    }
                });

                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });


        //Khi nhấn giữ từng item sẽ lựa gọi hoặc nt
        swipeMenuListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Booking booking = list.get(i);
                Dialog dialog = new Dialog(BookingActivity.this);
                dialog.setContentView(R.layout.one_connect);
                dialog.setCancelable(true);
                Window window = dialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (dialog != null && dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                CardView call = dialog.findViewById(R.id.cv_call);
                CardView sms = dialog.findViewById(R.id.cv_sms);

                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        phone = booking.getPhone();
                        makePhoneCall();
                    }
                });
// sms
                sms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog1 = new Dialog(BookingActivity.this);
                        dialog1.getWindow().getAttributes().windowAnimations = R.style.up_down;
                        dialog1.setContentView(R.layout.sendmasenger);
                        dialog1.setCancelable(true);
                        Window window = dialog1.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        if (dialog1 != null && dialog1.getWindow() != null) {
                            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }
                        final EditText sms = dialog1.findViewById(R.id.edtsms);
                        final TextView phone1 = dialog1.findViewById(R.id.so);
                        final TextView sendsms = dialog1.findViewById(R.id.sendsms);
                        phone1.setText(booking.getPhone());
                        sms.setText("Bạn có đặt sân vào ngày " + booking.getDatetime() + " đúng không?");
                        sendsms.setEnabled(false);
                        // check q` sms
                        if (checkPermission(Manifest.permission.SEND_SMS)) {
                            sendsms.setEnabled(true);
                        } else {
                            // xin q`
                            ActivityCompat.requestPermissions(BookingActivity.this, new String[]{Manifest.permission.SEND_SMS},
                                    SEND_SMS_PERMISSION_REQUEST_CODE);
                        }

                        sendsms.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String s = phone1.getText().toString();
                                String ed = sms.getText().toString();
                                if (s == null || s.length() == 0 || ed == null || ed.length() == 0) {
                                    return;
                                }
                                if (checkPermission(Manifest.permission.SEND_SMS)) {
                                    //SmsManager lớp có sẵn trong Android
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(s, null, ed, null, null);
                                    showDialog.show("Gửi tin nhắn thành công!");
                                    dialog1.dismiss();
                                } else {
                                    showDialog.show("Gửi tin nhắn thất bại!");
                                    dialog1.dismiss();
                                }
                            }
                        });

                        dialog1.findViewById(R.id.huysms).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog1.dismiss();
                            }
                        });
                        dialog1.show();

                    }
                });
                dialog.show();
                return false;
            }
        });

        //Khi ấn nút thêm
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(BookingActivity.this);
                dialog.setContentView(R.layout.add_booking);
                dialog.setCancelable(true);
                Window window = dialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (dialog != null && dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                //Khai báo biến
                TextInputEditText name = dialog.findViewById(R.id.edt_name);
                TextInputEditText phone = dialog.findViewById(R.id.edt_phone);
                TextInputEditText date = dialog.findViewById(R.id.edt_date);
                TextInputEditText time = dialog.findViewById(R.id.edt_time);
                TextInputEditText note = dialog.findViewById(R.id.edt_note);

                Button add = dialog.findViewById(R.id.btn_them_booking);
                Button cancle = dialog.findViewById(R.id.btn_huy_booking);
                date.setFocusable(false);
                time.setFocusable(false);
                //Chọn ảnh
                date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar calendar = Calendar.getInstance();
                        int d = calendar.get(Calendar.DAY_OF_MONTH);
                        int m = calendar.get(Calendar.MONTH);
                        int y = calendar.get(Calendar.YEAR);
                        datePickerDialog = new DatePickerDialog(BookingActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                final String NgayGD = dayOfMonth + "/" + (month + 1) + "/" + year;
                                date.setText(NgayGD);
                            }
                        }, y, m, d);
                        datePickerDialog.show();
                    }
                });

                time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Get Current Time
                        final Calendar c = Calendar.getInstance();
                        int mHour = c.get(Calendar.HOUR_OF_DAY);
                        int mMinute = c.get(Calendar.MINUTE);

                        // Launch Time Picker Dialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(BookingActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {

                                        time.setText(hourOfDay + ":" + minute);
                                    }
                                }, mHour, mMinute, false);
                        timePickerDialog.show();
                    }
                });

                //Khi ấn thêm
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nameText = name.getText().toString();
                        String phoneText = phone.getText().toString();
                        String dateText = date.getText().toString();
                        String timeText = time.getText().toString();
                        String noteText = note.getText().toString();

                        if (nameText.isEmpty() || phoneText.isEmpty() || dateText.isEmpty() || timeText.isEmpty()) {
                            showDialog.show("Không được để trống!");// lụm trên mạng format để tuân theo phone number
                        } else if (!phoneText.matches("(84|0[3|5|7|8|9])+([0-9]{8})\\b")) {
                            showDialog.show("Vui lòng nhập đúng số điện thoại!");
                        } else {

                            Booking booking = new Booking(1, nameText, phoneText, dateText + " " + timeText, String.valueOf(field.getId()), noteText, MainActivity.email);
                            if (bookingDao.them(booking)) {
                                dialog.dismiss();
                                showDialog.show("Thêm sân thành công!");
                                list.clear();
                                list.addAll(bookingDao.getAllByEmail(String.valueOf(field.getId()), MainActivity.email));
                                adapter.notifyDataSetChanged();
                            } else {
                                showDialog.show("Thêm sân thất bại!");
                            }
                        }
                    }
                });

                //Khi ấn nút hủy
                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        //Set sự kiện edit và xóa
        swipeLayout();
    }

    private void makePhoneCall() {
        final Dialog dialog = new Dialog(BookingActivity.this);
        dialog.getWindow().getAttributes().windowAnimations = R.style.up_down;
        dialog.setContentView(R.layout.call);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog != null && dialog.getWindow() != null) {
           //set màu bg thành trong suốt
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        final TextView phonex = dialog.findViewById(R.id.number);
        phonex.setText(phone);
        dialog.findViewById(R.id.goi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // (trim cắt khoản trắng của chuổi ở trước và sau,) 1.phone không rỗng
                if (!phone.trim().isEmpty()) {
                    // kt đã có quyền hay chưa Manifest.permission. != thì chưa cấp q
                    if (ContextCompat.checkSelfPermission(BookingActivity.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        //ActivityCompat xin quyền
                        ActivityCompat.requestPermissions(BookingActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                    } else {
                        // chuổi sdt để gọi
                        String dial = "tel:" + phone;
                        // Intent thực hiện cuộc gọi dựa trên sdt
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                        dialog.dismiss();
                    }
                } else {
                    showDialog.show("Gọi thất bại!");
                }
            }
        });
        dialog.findViewById(R.id.huy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void swipeLayout() {
        //Thanh Swipe
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem editItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                editItem.setBackground(R.color.edit);
                // set item width
                editItem.setWidth(170);
                // set item title font color
                editItem.setIcon(R.drawable.ic_baseline_edit_24);
                // add to menu
                menu.addMenuItem(editItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(R.color.delete);
                // set item width
                deleteItem.setWidth(170);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_baseline_delete_24);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        swipeMenuListView.setMenuCreator(creator);

        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                //lấy vị trí
                final Booking booking1;
                switch (index) {
                    //Chỉnh sửa
                    case 0:
                        booking1 = list.get(position);
                        Dialog dialog = new Dialog(BookingActivity.this);
                        dialog.setContentView(R.layout.add_booking);
                        dialog.setCancelable(true);
                        Window window = dialog.getWindow();
                        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        if (dialog != null && dialog.getWindow() != null) {
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }
                        //Khai báo biến
                        TextInputEditText name = dialog.findViewById(R.id.edt_name);
                        TextInputEditText phone = dialog.findViewById(R.id.edt_phone);
                        TextInputEditText date = dialog.findViewById(R.id.edt_date);
                        TextInputEditText time = dialog.findViewById(R.id.edt_time);
                        TextInputEditText note = dialog.findViewById(R.id.edt_note);
                        Button add = dialog.findViewById(R.id.btn_them_booking);
                        add.setText("SỬA");
                        Button cancle = dialog.findViewById(R.id.btn_huy_booking);
                        date.setFocusable(false);
                        time.setFocusable(false);
                        TextView title = dialog.findViewById(R.id.titleView);
                        time.setText("SỬA LỊCH ĐẶT SÂN");

                        //Tách ngày, thời gian riêng
                        String datex = booking1.getDatetime().split("\\s")[0];
                        String timex = booking1.getDatetime().split("\\s")[1];

                        //set dữ liệu đã có sẵn
                        name.setText(booking1.getName());
                        phone.setText(booking1.getPhone());
                        date.setText(datex);
                        time.setText(timex);
                        note.setText(booking1.getNote());

                        //Chọn ngày
                        date.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Calendar calendar = Calendar.getInstance();
                                int d = calendar.get(Calendar.DAY_OF_MONTH);
                                int m = calendar.get(Calendar.MONTH);
                                int y = calendar.get(Calendar.YEAR);
                                datePickerDialog = new DatePickerDialog(BookingActivity.this, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        final String NgayGD = dayOfMonth + "/" + (month + 1) + "/" + year;
                                        date.setText(NgayGD);
                                    }
                                }, y, m, d);
                                datePickerDialog.show();
                            }
                        });

                        time.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Get Current Time
                                final Calendar c = Calendar.getInstance();
                                int mHour = c.get(Calendar.HOUR_OF_DAY);
                                int mMinute = c.get(Calendar.MINUTE);

                                // Launch Time Picker Dialog
                                TimePickerDialog timePickerDialog = new TimePickerDialog(BookingActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                                  int minute) {

                                                time.setText(hourOfDay + ":" + minute);
                                            }
                                        }, mHour, mMinute, false);
                                timePickerDialog.show();
                            }
                        });

                        //Khi ấn thêm
                        add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String nameText = name.getText().toString();
                                String phoneText = phone.getText().toString();
                                String dateText = date.getText().toString();
                                String timeText = time.getText().toString();
                                String noteText = note.getText().toString();

                                if (nameText.isEmpty() || phoneText.isEmpty() || dateText.isEmpty() || timeText.isEmpty()) {
                                    showDialog.show("Không được để trống!");
                                } else if (!phoneText.matches("(84|0[3|5|7|8|9])+([0-9]{8})\\b")) {
                                    showDialog.show("Vui lòng nhập đúng số điện thoại!");
                                } else {
                                    Booking x = new Booking(booking1.getId(), nameText, phoneText, dateText + " " + timeText, String.valueOf(field.getId()), noteText, MainActivity.email);
                                    Log.d(TAG, x.toString());
                                    if (bookingDao.sua(x)) {
                                        dialog.dismiss();
                                        showDialog.show("Sửa sân thành công!");
                                        list.clear();
                                        list.addAll(bookingDao.getAllByEmail(String.valueOf(field.getId()), MainActivity.email));
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        showDialog.show("Sửa sân thất bại!");
                                    }
                                }
                            }
                        });

                        //Khi ấn nút hủy
                        cancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                    //xóa
                    case 1:
                        booking1 = list.get(position);
                        final AlertDialog.Builder builder2 = new AlertDialog.Builder(BookingActivity.this);
                        builder2.setTitle("Cảnh báo");
                        builder2.setMessage("Bạn có chắc muốn xóa?");
                        builder2.setNegativeButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //code yes để xóa
                                if (bookingDao.xoa(booking1)) {
                                    showDialog.show("Xóa thành công!");
                                    list.clear();
                                    list.addAll(bookingDao.getAllByEmail(String.valueOf(field.getId()), MainActivity.email));
                                    adapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                } else {
                                    showDialog.show("Xóa thất bại!");
                                }

                            }
                        });
                        builder2.setPositiveButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        final AlertDialog dialogx = builder2.create();
                        dialogx.getWindow().getAttributes().windowAnimations = R.style.up_down;
                        dialogx.show();

                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        menu.findItem(R.id.logout).setVisible(true);
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

    private void init() {
        swipeMenuListView = findViewById(R.id.rc_booking_field);
        bookingDao = new BookingDao(this);
        historyDao = new HistoryDao(this);
        add = findViewById(R.id.fl_add_booking);
        showDialog = new ShowDialog(this);
    }

    @Override
    //cấp quyền gọi
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                showDialog.show("Vui lòng cấp quyền để gọi!");
            }
        }
    }

    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(BookingActivity.this, permission);
        return (checkPermission == PackageManager.PERMISSION_GRANTED);
    }
}