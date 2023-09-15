package com.taufiqskripsiapp.masbro;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.taufiqskripsiapp.masbro.Fragment.SearchFragment;
import com.taufiqskripsiapp.masbro.Fragment.ShoppingFragment;
import com.taufiqskripsiapp.masbro.Utils.BoyerMooreSearch;
import com.taufiqskripsiapp.masbro.Utils.ConVar;
import com.taufiqskripsiapp.masbro.Utils.MyDataManager;
import com.taufiqskripsiapp.masbro.ViewModel.AppSharedViewModel;
import com.taufiqskripsiapp.masbro.ViewModel.ProfileViewModel;
import com.taufiqskripsiapp.masbro.databinding.ActivityMainBinding;
import com.taufiqskripsiapp.masbro.Fragment.HomeFragment;
import com.taufiqskripsiapp.masbro.Fragment.ProfileFragment;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    String TAG = "DEBUG";

    ActivityMainBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser user;

    FloatingActionButton fabScan;
    private AppBarLayout appBarLayout;
    private MaterialToolbar toolbar;

    AppSharedViewModel appSharedViewModel;
    ProfileViewModel profileViewModel;
    MyDataManager myDataManager;

    List<String> allProdukKeys;

    BoyerMooreSearch boyerMooreSearch;
    String keyProduk = null, pilihanToko = null, ketersediaanToko = null;
    Boolean isBarcodeFound = false, isFirebaseDataReady = false, lanjutScan = false;
    Boolean ketersediaan = false;

    DatabaseReference produkRef, shoppingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        myDataManager = (MyDataManager) MainActivity.this.getApplication();
        appSharedViewModel = myDataManager.getAppSharedViewModel();
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        produkRef = FirebaseDatabase.getInstance().getReference(ConVar.PRODUK_DB_REF);
        shoppingRef = FirebaseDatabase.getInstance().getReference("ShopApp/ShoppingCart");

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

        allProdukKeys = new ArrayList<>();
        produkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot produkSnapshot : snapshot.getChildren()){
                    String produkKey = produkSnapshot.getKey();
                    allProdukKeys.add(produkKey);
                }
                isFirebaseDataReady = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fabScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Observer<String> tokoObserver = new Observer<String>() {
                    @Override
                    public void onChanged(String tokoPilihan) {
                        pilihanToko = tokoPilihan;
//                        if (pilihanToko == null) {
//                            Log.d(TAG, "onChanged: Snackbar harusnya muncul");
//                            Toast.makeText(MainActivity.this, "Anda Belum Memilih Toko Pada Home, Silahkan Pilih Terlebih Dahulu", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.d(TAG, "onChanged: Snackbar harusnya tidak muncul");
//                            ScanBarcode();
//                        }
                        appSharedViewModel.getPilihanTokoLiveData().removeObserver(this);
                    }
                };
                appSharedViewModel.getPilihanTokoLiveData().observe(MainActivity.this, tokoObserver);

                if (pilihanToko == null) {
                    View rootView = findViewById(android.R.id.content);
                    Snackbar snackbar = Snackbar.make(rootView, "Anda Belum Menentukan Toko Pada Home. Silahkan Pilih Terlebih Dahulu", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Tutup", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                } else {
                    ScanBarcode();
                }
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
        if(result.getContents() != null && isFirebaseDataReady){

            isBarcodeFound = false;
            keyProduk = null;

            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View view = inflater.inflate(R.layout.custom_dialog_layout_scanbarcode, null);
            String scannedBarcode = result.getContents();
            TextView namaProdukET = view.findViewById(R.id.TextNamaProduk);
            TextView barcodeET = view.findViewById(R.id.TextBarcode);
            TextView hargaProdukET = view.findViewById(R.id.TextHargaProduk);
            ImageView imageIV = view.findViewById(R.id.IVGambarProduk);



//            allProdukKeys = new ArrayList<>();

            boyerMooreSearch = new BoyerMooreSearch(scannedBarcode);

            for(String key : allProdukKeys){
                int position = boyerMooreSearch.search(key);
                if(position != key.length()){
                    isBarcodeFound = true;
                    keyProduk = key;
                    break;
                }
            }
            if (isBarcodeFound){
                appSharedViewModel.getPilihanTokoLiveData().observe(MainActivity.this, new Observer<String>() {
                    @Override
                    public void onChanged(String tokoPilihan) {
                        if(tokoPilihan != null){
                            if(tokoPilihan.equals(ConVar.TOKO_FIKTIF)){
                                pilihanToko = ConVar.HARGA_FIKTIF;
                                ketersediaanToko = ConVar.KETERSEDIAAN_FIKTIF;
                            } else if (tokoPilihan.equals(ConVar.TOKO_NOARI)){
                                pilihanToko = ConVar.HARGA_NOARI;
                                ketersediaanToko = ConVar.KETERSEDIAAN_NOARI;
                            }
                        }

                    }
                });

                produkRef.child(keyProduk).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ketersediaan = snapshot.child("ketersediaan").child(ketersediaanToko).getValue(Boolean.class);
                        if(!ketersediaan){
//                            Toast.makeText(MainActivity.this, "Produk Tidak Tersedia Ditoko ini", Toast.LENGTH_SHORT).show();
                            new MaterialAlertDialogBuilder(MainActivity.this)
                                    .setTitle("Produk Tidak Ditemukan Di Toko")
                                    .setMessage("Silahkan Coba lagi atau input manual dari menu Search")
                                    .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            ScanBarcode();
                                        }
                                    })
                                    .show();
                        } else{
                            String nama = snapshot.child("nama").getValue().toString();
                            String barcode = snapshot.child("barcode").getValue().toString();
                            String uri = snapshot.child("gambar").getValue().toString();
                            Integer harga = snapshot.child("harga").child(pilihanToko).getValue(Integer.class);


                            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
                            String hargaFormated = numberFormat.format(harga).replace("Rp", "Rp. ");

                            namaProdukET.setText(nama);
                            barcodeET.setText(barcode);
                            hargaProdukET.setText(hargaFormated);
                            Glide.with(MainActivity.this)
                                    .load(uri)
                                    .into(imageIV);

                            new MaterialAlertDialogBuilder(MainActivity.this)
                                    .setTitle("Produk Ditemukan")
                                    .setView(view)
                                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("Tambah", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            appSharedViewModel.setKeyProdukHasilScanLiveData(keyProduk);
                                            Toast.makeText(MainActivity.this, "Data Sudah Ditambah", Toast.LENGTH_SHORT).show();
                                            ScanBarcode();
                                        }
                                    })
                                    .show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            } else {
                Toast.makeText(this, "Produk Yang Anda Cari Tidak Ditemukan Di Toko Ini", Toast.LENGTH_SHORT).show();
            }


        }
    });

    private void replacedFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}