package com.subi.soccerbooking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.subi.soccerbooking.adapter.ShowDialog;
import com.subi.soccerbooking.database.TaiKhoanDao;
import com.subi.soccerbooking.model.User;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText user, pass, pass2;
    private Button register, close;
    private ShowDialog dialog;
    private String TAG = "RegisterActivity";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

        //Cài đặt tiêu đề
        setTitle("ĐĂNG KÝ");

        //Khi nhấn nút đóng
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Khi ấn đăng ký
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tk, mk, mk2;
                tk = user.getText().toString();
                mk = pass.getText().toString();
                mk2 = pass2.getText().toString();

                if (checkValidate(tk, mk, mk2)) {
                    progressDialog.setMessage("Đang đăng ký...");
                    progressDialog.show();
                    TaiKhoanDao taiKhoanDao = new TaiKhoanDao(RegisterActivity.this);
                    if (taiKhoanDao.them(new User(tk, mk))) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.putExtra("tk", tk);
                        intent.putExtra("mk", mk);
                        progressDialog.dismiss();
                        startActivity(intent);
                    } else {
                        dialog.show("Tài khoản đã tồn tại");
                        progressDialog.dismiss();
                    }

                }

            }
        });
    }

    private void init() {
        user = findViewById(R.id.edtTkDK);
        pass = findViewById(R.id.edtMkDK);
        pass2 = findViewById(R.id.edtMkDK2);
        register = findViewById(R.id.btnDangKy);
        close = findViewById(R.id.btnNhapLai);
        dialog = new ShowDialog(this);
        progressDialog = new ProgressDialog(this);
    }

    private Boolean checkValidate(String tk, String mk, String mk2) {
        Boolean check = false;
        if (tk.isEmpty() || mk.isEmpty() || mk2.isEmpty()) {
            dialog.show("Không được để trống!");
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(tk).matches()) {
            dialog.show("Không đúng dạng email!");
        } else if (!mk.equals(mk2)) {
            dialog.show("Mật khẩu không khớp nhau!");
        } else {
            check = true;
        }

        return check;
    }
}