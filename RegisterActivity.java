package com.taufiqskripsiapp.masbro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.taufiqskripsiapp.masbro.Utils.MasbroUtils;

import android.view.MotionEvent;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText editName, editEmail, editPassword, editConfPassword;
    String name, email, password, confPassword;
    Button registerButton;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    ProgressBar progressBar;
    Boolean isAdmin;

    View rootView;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            toMainMenu();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.RegisterProgressBar);
        database = FirebaseDatabase.getInstance();


        editName = findViewById(R.id.EditName);
        editEmail = findViewById(R.id.EditEmail);
        editPassword = findViewById(R.id.EditPassword);
        editConfPassword = findViewById(R.id.EditConfPassword);

        registerButton = findViewById(R.id.RegisterButton);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDataStored();
            }
        });

        rootView = findViewById(android.R.id.content);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MasbroUtils.hideKeyboard(RegisterActivity.this, rootView);
                return false;
            }
        });


    }

    public void LoginClicked(View view){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void UserDataStored(){
        MasbroUtils.hideKeyboard(RegisterActivity.this, rootView);
        progressBar.setVisibility(View.VISIBLE);

        name = String.valueOf(editName.getText());
        email = String.valueOf(editEmail.getText());
        password = String.valueOf(editPassword.getText());
        confPassword = String.valueOf(editConfPassword.getText());
        isAdmin = false;

        if (name.isEmpty()){
            editName.setError("Nama Harus Diisi");
            progressBar.setVisibility(View.GONE);
        }
        if (email.isEmpty()){
            editEmail.setError("Email Harus Diisi");
            progressBar.setVisibility(View.GONE);
        }
        if (password.isEmpty()){
            editPassword.setError("Password Harus Diisi");
            progressBar.setVisibility(View.GONE);
        }

        if (confPassword.isEmpty()){
            editConfPassword.setError("Password Harus Dikonfirmasi");
            progressBar.setVisibility(View.GONE);
        } else if (!password.equals(confPassword)){
            editConfPassword.setError("Password Tidak Cocok");
            progressBar.setVisibility(View.GONE);
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user != null){
                                    String uid = user.getUid();

                                    DatabaseReference usersData = database.getReference("ShopApp/Account/User");
                                    DatabaseReference userData = usersData.child(uid);
                                    userData.child("isAdmin").setValue(isAdmin);
                                    userData.child("nama").setValue(name)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressBar.setVisibility(View.GONE);
                                                    // Sign in success, update UI with the signed-in user's information
                                                    Toast.makeText(RegisterActivity.this, "Akun Berhasil Dibuat",
                                                            Toast.LENGTH_SHORT).show();
                                                    toMainMenu();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(RegisterActivity.this, "Akun Gagal Dibuat",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                            } else {
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user.
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void toMainMenu(){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }

}