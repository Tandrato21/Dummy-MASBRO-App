package com.taufiqskripsiapp.masbro.Onboarding;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.taufiqskripsiapp.masbro.LoginActivity;
import com.taufiqskripsiapp.masbro.R;

public class Onboarding extends AppCompatActivity {

    //Deklarasi Elemen Layout
    public Button btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Menyembunyikan bilah notifikasi (notification bar)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // TODO: Menampilkan kembali bilah notifikasi (notification bar)
        // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); CATATAN

        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("isFirstTime", true);

        if (isFirstTime) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();
        } else {
            Intent intent = new Intent(Onboarding.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }


        btnSkip = findViewById(R.id.onbSkipBtn);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSkipClicked();

            }
        });

    }

    public void btnSkipClicked(){

        Intent intent = new Intent(Onboarding.this, LoginActivity.class);
        startActivity(intent);

    }
}