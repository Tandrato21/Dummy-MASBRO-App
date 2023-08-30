package com.taufiqskripsiapp.masbro;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.taufiqskripsiapp.masbro.Fragment.SearchFragment;
import com.taufiqskripsiapp.masbro.Fragment.ShoppingFragment;
import com.taufiqskripsiapp.masbro.databinding.ActivityMainBinding;
import com.taufiqskripsiapp.masbro.Fragment.HomeFragment;
import com.taufiqskripsiapp.masbro.Fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;

    FloatingActionButton fabScan;
    private AppBarLayout appBarLayout;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        appBarLayout = findViewById(R.id.topAppBar);
        toolbar = findViewById(R.id.appTitleToolbar);
        fabScan = findViewById(R.id.scanFab);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        replacedFragment(new HomeFragment());
        toolbar.setTitle("Home");

        binding.bottomNavbar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replacedFragment(new HomeFragment());
                toolbar.setTitle("Home");
            } else if (itemId == R.id.shopping) {
                replacedFragment(new ShoppingFragment());
                toolbar.setTitle("Daftar Belanja");
            } else if (itemId == R.id.search) {
                replacedFragment(new SearchFragment());
                toolbar.setTitle("Cari Produk");
            } else if (itemId == R.id.profil){
                replacedFragment((new ProfileFragment()));
                toolbar.setTitle("Profile");
            }
            return true;
        });


        fabScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanBarcode();
            }
        });

    }

    private void ScanBarcode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Arahkan kamera ke barcode produk");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null ){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            }).show();
        }
    });



    private void replacedFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}