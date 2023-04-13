package com.subi.soccerbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class SplashActivity extends AppCompatActivity {
private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView = findViewById(R.id.footkick);
        //Load ảnh động khi mở app
        Glide.with(this).load(R.drawable.footkick).into(imageView);
        //Dùng cài đặt sau 2.3 giây màn hình tự chuyển
        Thread bamgio = new Thread() {


            public void run() {
                try {
                    sleep(3000);
                } catch (Exception e) {

                } finally {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.bottom, R.anim.nothing);
                }
            }
        };
        bamgio.start();
    }
}