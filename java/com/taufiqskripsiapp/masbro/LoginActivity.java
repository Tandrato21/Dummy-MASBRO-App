package com.taufiqskripsiapp.masbro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.taufiqskripsiapp.masbro.Utils.MasbroUtils;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText editEmail, editPassword;
    String email, password;
    Button loginButton;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    ProgressBar progressBar;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            toMainMenu();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.EditEmail);
        editPassword = findViewById(R.id.EditPassword);
        loginButton = findViewById(R.id.LoginButton);
        progressBar = findViewById(R.id.LoginProgressBar);

        View rootView = findViewById(android.R.id.content);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MasbroUtils.hideKeyboard(LoginActivity.this, rootView);
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                MasbroUtils.hideKeyboard(LoginActivity.this, rootView);
                email = String.valueOf(editEmail.getText());
                password = String.valueOf(editPassword.getText());

                if (email.isEmpty()){
                    editEmail.setError("Email Harus Diisi");
                    progressBar.setVisibility(View.GONE);
                } else if (password.isEmpty()){
                    editPassword.setError("Password Harus Diisi");
                    progressBar.setVisibility(View.GONE);
                } else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(LoginActivity.this, "Authentication Successful.",Toast.LENGTH_SHORT).show();
                                        toMainMenu();
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });


    }


    public void registerClicked(View view){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void forgotPassword(View view){
        Intent intent = new Intent(LoginActivity.this, ResetPassActivity.class);
        startActivity(intent);
    }


    public void toMainMenu(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }


}