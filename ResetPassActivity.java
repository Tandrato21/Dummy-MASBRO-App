package com.taufiqskripsiapp.masbro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ResetPassActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    Button verifEmailButton, backToLogin;
    TextInputEditText editEmail;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        firebaseAuth = FirebaseAuth.getInstance();

        verifEmailButton = findViewById(R.id.VerificationButton);
        backToLogin = findViewById(R.id.buttonBackToLogin);

        editEmail = findViewById(R.id.EditEmail);

        progressBar = findViewById(R.id.ResetProgressBar);

        verifEmailButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = editEmail.getText().toString().trim();
            hideKeyboard();
            resetPassword(email);
        });

        backToLogin.setOnClickListener(view -> {
            BackToLogin();
        });

        View rootView = findViewById(android.R.id.content);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

    }

    private void BackToLogin() {
        Intent intent = new Intent(ResetPassActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }


    private void resetPassword(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Reset password email berhasil dikirim
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Informasi");
                        builder.setMessage("Link untuk recovery telah dikirim ke email Anda.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Kode yang akan dijalankan saat tombol OK diklik
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

//                        Toast.makeText(ResetPassActivity.this, "Email reset password telah dikirim.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Gagal mengirim email reset password
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            // Email tidak terdaftar
                            Snackbar.make(findViewById(android.R.id.content), "Email tidak terdaftar.", Snackbar.LENGTH_LONG).show();
//                            Toast.makeText(ResetPassActivity.this, "Email tidak terdaftar.", Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            // Format email tidak valid
                            Snackbar.make(findViewById(android.R.id.content), "Format email tidak valid.", Snackbar.LENGTH_LONG).show();
//                            Toast.makeText(ResetPassActivity.this, "Format email tidak valid.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            // Error lainnya
                            Snackbar.make(findViewById(android.R.id.content), "Gagal mengirim email reset password.", Snackbar.LENGTH_LONG).show();
//                            Toast.makeText(ResetPassActivity.this, "Gagal mengirim email reset password.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}