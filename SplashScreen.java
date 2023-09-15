package com.taufiqskripsiapp.masbro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.taufiqskripsiapp.masbro.Onboarding.Onboarding;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Menyembunyikan bilah notifikasi (notification bar)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // TODO: Menampilkan kembali bilah notifikasi (notification bar)
        // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); CATATAN

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), Onboarding.class));
                finish();
            }
        }, 1000L); //3000 L = 3 detik
    }
}