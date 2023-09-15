//Catatan : Kode ProductInputActivity.java
package com.taufiqskripsiapp.masbro.Fragment.SearchFrag;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;
//import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.taufiqskripsiapp.masbro.MainActivity;
import com.taufiqskripsiapp.masbro.R;
import com.taufiqskripsiapp.masbro.Utils.ConVar;
import com.taufiqskripsiapp.masbro.Utils.MasbroUtils;
import com.taufiqskripsiapp.masbro.Utils.MyDataManager;
import com.taufiqskripsiapp.masbro.ViewModel.AppSharedViewModel;
import com.taufiqskripsiapp.masbro.ViewModel.ProfileViewModel;
import com.taufiqskripsiapp.masbro.ViewModel.SharedViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProductInputActivity extends AppCompatActivity {

//    private static final int PICK_IMAGE_REQUEST = 1;
//    private static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView inputProductIV;
    private Bitmap capturedImage, galeriImage;
    ImageView imageViewPreview, imageViewDelete, ivCamera, ivGallery;
    Bitmap toFirebaseBitmap;
    String imageUri;

    TextInputEditText barcodeInputEditText, namaProdukET, merekProdukET, hargaET ,hargaNoariET, hargaFiktifET, deskripsiET;
    MaterialSwitch switchNoari, switchFiktif;
    AutoCompleteTextView kategoriDropdown, kemasanDropdown;
    BottomSheetDialog bottomSheetDialog;
    ProgressBar progressBar;
    ProfileViewModel profileViewModel;
    Button submit;

    MyDataManager myDataManager;
    AppSharedViewModel appSharedViewModel;

    Boolean isAdmin, superAdmin;
    Boolean actionCompleteData = false;
    String adminToko;
    ArrayList<String>  kategoriList, kemasanList;
    ArrayAdapter<String> kategoriAdapter, kemasanAdapter;
    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_input_superadmin);
        String TAG = "TAG";
//        myDataManager = (MyDataManager) getApplication();
//        appSharedViewModel = myDataManager.getAppSharedViewModel();
        myDataManager = (MyDataManager) ProductInputActivity.this.getApplication();
        appSharedViewModel = myDataManager.getAppSharedViewModel();
//        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        actionCompleteData = getIntent().getBooleanExtra("actionCompleteData", actionCompleteData);
//        if (actionCompleteData) {
//
//        } else {
//
//        }

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        profileViewModel.getisAdminLiveData().observe(this, admin -> {
            isAdmin = admin;
            profileViewModel.getSuperAdminLiveData().observe(this, adminSuper -> {
                superAdmin = adminSuper;
                profileViewModel.getAdminTokoLiveData().observe(this, tokoAdmin ->{
                    adminToko = tokoAdmin;
                });
                setupLayout();
            });
        });
        // Semua kode listener tidak lagi ditaruh di sini, semua di pindahkan ke inisialiasilistener()
    }

    private void inisialisasiArrayList() {
//        rootView = findViewById(R.id.ScrollLayout);
//        inputProductIV = findViewById(R.id.InputProductImageView);

        kategoriDropdown = findViewById(R.id.KategoriAutoCompleteText);
        kemasanDropdown = findViewById(R.id.KemasanAutoCompleteText);


        kategoriList = new ArrayList<>();
        kategoriList.add("Makanan");
        kategoriList.add("Minuman");
        kategoriList.add("Produk Kebersihan");
        kategoriList.add("Kosmetik");

        kategoriAdapter = new ArrayAdapter<>(this, R.layout.item_list, kategoriList);
        kategoriDropdown.setAdapter(kategoriAdapter);

        kemasanList = new ArrayList<>();
        kemasanList.add("Plastik");
        kemasanList.add("Karton");
        kemasanList.add("Kaleng");
        kemasanList.add("Botol Plastik");

        kemasanAdapter = new ArrayAdapter<>(this, R.layout.item_list, kemasanList);
        kemasanDropdown.setAdapter(kemasanAdapter);

    }

    private void setupLayout() {
        if(isAdmin != null && isAdmin && superAdmin != null && superAdmin ){
            setContentView(R.layout.activity_product_input_superadmin);

        } else if (isAdmin!=null && isAdmin) {
            setContentView(R.layout.activity_product_input_admin);
        }
        //Inisialisasi sehingga findViewById tidak null

        Log.d("DEBUG", "setupLayout: actioncompletedata " + actionCompleteData);
        //TODO : bagian ini akan ditinggalkan dahulu. jika masalah tidak muncul data pada edittext yg berkaitan,
        // maka lakukan cara sederhana untup update harga dan ketersediaan dari searchFragment
        if(actionCompleteData){
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String actionBarTitle;
            getSupportActionBar().setTitle("Update Data Produk");
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            if(superAdmin){
                namaProdukET = findViewById(R.id.ETNamaProduk);
                merekProdukET = findViewById(R.id.ETMerekProduk);
                deskripsiET = findViewById(R.id.ETDeskripsi);
                kategoriDropdown = findViewById(R.id.KategoriAutoCompleteText);
                kemasanDropdown = findViewById(R.id.KemasanAutoCompleteText);
                deskripsiET = findViewById(R.id.ETDeskripsi);
                hargaNoariET = findViewById(R.id.HargaTokoNoari);
                hargaFiktifET = findViewById(R.id.HargaTokoFiktifJaya);
                switchFiktif = findViewById(R.id.SwitchFiktifJaya);
                switchNoari = findViewById(R.id.SwitchNoari);
                inputProductIV = findViewById(R.id.InputProductImageView);
//                barcodeInputEditText = findViewById(R.id.ETProdukBarcode);
                appSharedViewModel.getProdukDataMap().observe(this, new Observer<HashMap<String, Object>>() {
                    @Override
                    public void onChanged(HashMap<String, Object> dataMap) {
                        if(dataMap != null){
                            String namaProduk = (String) dataMap.get("namaProduk");
                            String merek = (String) dataMap.get("merek");
                            String kemasan = (String) dataMap.get("kemasan");
                            String kategori = (String) dataMap.get("kategori");
                            String deskripsi = (String) dataMap.get("deskripsi");
                            String uri = (String) dataMap.get("uri");
//                            Integer hargaNoari = (Integer) dataMap.get("hargaNoari");
//                            Integer hargaFiktif = (Integer) dataMap.get("hargaFiktif");
//                            Boolean ketersediaanNoari = (Boolean) dataMap.get("ketersediaanNoari");
//                            Boolean ketersediaanFiktif = (Boolean) dataMap.get("ketersediaanFiktif");

                            Log.d("DEBUG", "onChanged: get produkdatamap" + namaProduk + merek);
                            namaProdukET.setText(namaProduk);
                            merekProdukET.setText(merek);
                            kemasanDropdown.setText(kemasan);
                            kategoriDropdown.setText(kategori);
                            deskripsiET.setText(deskripsi);

                            if(dataMap.containsKey("ketersediaanFiktif")){
                                Integer hargaFiktif = (Integer) dataMap.get("hargaFiktif");
                                Boolean ketersediaanFiktif = (Boolean) dataMap.get("ketersediaanFiktif");
                                if(ketersediaanFiktif){
                                    switchFiktif.setChecked(true);
                                    hargaFiktifET.setText(String.valueOf(hargaFiktif));
                                }else{
                                    switchFiktif.setChecked(false);
                                }
                            } else if (dataMap.containsKey("ketersediaanNoari")) {
                                Integer hargaNoari = (Integer) dataMap.get("hargaNoari");
                                Boolean ketersediaanNoari = (Boolean) dataMap.get("ketersediaanNoari");
                                if(ketersediaanNoari){
                                    switchNoari.setChecked(true);
                                    hargaNoariET.setText(String.valueOf(hargaNoari));
                                }else{
                                    switchNoari.setChecked(false);
                                }
                            }
                            Glide.with(getApplicationContext())
                                    .load(uri)
                                    .into(inputProductIV);
                        }
                    }
                });

            }
        } else {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String actionBarTitle;
            getSupportActionBar().setTitle("Input Data Produk");
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);


        }
        inisialisasiArrayList();
        inisialisasiListeners();

        barcodeInputEditText = findViewById(R.id.ETProdukBarcode);
        appSharedViewModel.getBarcodeScanValue().observe(this, barcode ->{
            if(barcode != null){
                barcodeInputEditText.setText(barcode);
            } else {
                Log.d("TAG", "onCreate: scannedBarcode value adalah null");
            }
        });

    }

    private void inisialisasiListeners() {
        submit = findViewById(R.id.ButtonSubmit);
        progressBar =findViewById(R.id.progressBarInline);
        rootView = findViewById(R.id.ScrollLayout);
        inputProductIV = findViewById(R.id.InputProductImageView);

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MasbroUtils.hideKeyboard(ProductInputActivity.this, rootView);
                return false;
            }
        });

//        inputProductIV = findViewById(R.id.InputProductImageView);
        inputProductIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputProductImage();
            }
        });

        kemasanDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = parent.getItemAtPosition(position).toString();
            Toast.makeText(this, selectedItem, Toast.LENGTH_SHORT).show();
        });

        kategoriDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = parent.getItemAtPosition(position).toString();
            Toast.makeText(this, selectedItem, Toast.LENGTH_SHORT).show();
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dbProduk = FirebaseDatabase.getInstance().getReference(ConVar.PRODUK_DB_REF);

                submit.setEnabled(false);
                submit.setText("");
                progressBar.setVisibility(View.VISIBLE);

                namaProdukET = findViewById(R.id.ETNamaProduk);
                merekProdukET = findViewById(R.id.ETMerekProduk);
                deskripsiET = findViewById(R.id.ETDeskripsi);
                //Ada lagi ImageView, Kategori, Kemasan yang sudah di inisialisasi.

                String barcode = barcodeInputEditText.getText().toString().trim();
                String namaProduk = namaProdukET.getText().toString().trim();
                String merek = merekProdukET.getText().toString().trim();
                String kategori = kategoriDropdown.getText().toString().trim();
                String kemasan = kemasanDropdown.getText().toString().trim();
                String deskripsi = deskripsiET.getText().toString().trim();

                String kunciProduk = barcode + "-" + namaProduk.replace(" ", "-").toLowerCase();

                DatabaseReference dataProduk = dbProduk.child(kunciProduk);
                int hargaNoari = 0, hargaFiktif = 0, hargaProduk = 0;
                boolean tersediaNoari, tersediaFiktif;

                if (!superAdmin){
                    hargaET = findViewById(R.id.HargaProduk);
                    hargaProduk = Integer.parseInt(hargaET.getText().toString());

                } else {
                    hargaNoariET = findViewById(R.id.HargaTokoNoari);
                    hargaFiktifET = findViewById(R.id.HargaTokoFiktifJaya);
                    switchFiktif = findViewById(R.id.SwitchFiktifJaya);
                    switchNoari = findViewById(R.id.SwitchNoari);
                    if (switchNoari.isChecked()) {
                        String hargaNoariText = hargaNoariET.getText().toString().trim();
                        if(!hargaNoariText.isEmpty()){
                            hargaNoari = Integer.parseInt(hargaNoariET.getText().toString());
                        }else{
                            Toast.makeText(ProductInputActivity.this, "Mohon isi harga untuk Noari", Toast.LENGTH_SHORT).show();
                            submit.setEnabled(true);
                            submit.setText("UPLOAD PRODUK");
                            progressBar.setVisibility(View.GONE);
                        }

                    } else if (switchFiktif.isChecked()) {
                        String hargaFiktifText = hargaFiktifET.getText().toString().trim();
                        if(!hargaFiktifText.isEmpty()){
                            hargaFiktif = Integer.parseInt(hargaFiktifET.getText().toString());
                        } else{
                            Toast.makeText(ProductInputActivity.this, "Mohon isi harga untuk Noari", Toast.LENGTH_SHORT).show();
                            submit.setEnabled(true);
                            submit.setText("UPLOAD PRODUK");
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }



                if(superAdmin){

                    if (namaProduk.isEmpty() || merek.isEmpty() || kategori.isEmpty() || kemasan.isEmpty() || deskripsi.isEmpty()) {
                        Toast.makeText(ProductInputActivity.this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
                        submit.setEnabled(true);
                        submit.setText("UPLOAD PRODUK");
                        progressBar.setVisibility(View.GONE);
                    } else {
                        // Jika semua field telah diisi, lanjutkan proses upload ke Firebase
                        // Kode untuk upload ke Firebase di sini
                        dataProduk.child("barcode").setValue(barcode);
                        dataProduk.child("nama").setValue(namaProduk);
                        dataProduk.child("merek").setValue(merek);
                        dataProduk.child("kategori").setValue(kategori);
                        dataProduk.child("kemasan").setValue(kemasan);
                        dataProduk.child("deskripsi").setValue(deskripsi);

                        if (switchNoari.isChecked()){
                            tersediaNoari = true;
                            dataProduk.child("ketersediaan").child(ConVar.KETERSEDIAAN_NOARI).setValue(tersediaNoari);
                            dataProduk.child("harga").child(ConVar.HARGA_NOARI).setValue(hargaNoari);
                        } else {
                            tersediaNoari = false;
                            dataProduk.child("ketersediaan").child(ConVar.KETERSEDIAAN_NOARI).setValue(tersediaNoari);
                            dataProduk.child("harga").child(ConVar.HARGA_NOARI).setValue(null);
                        }

                        if (switchFiktif.isChecked()){
                            tersediaFiktif = true;
                            dataProduk.child("ketersediaan").child(ConVar.KETERSEDIAAN_FIKTIF).setValue(tersediaFiktif);
                            dataProduk.child("harga").child(ConVar.HARGA_FIKTIF).setValue(hargaFiktif);
                        } else {
                            tersediaFiktif = false;
                            dataProduk.child("ketersediaan").child(ConVar.KETERSEDIAAN_FIKTIF).setValue(tersediaFiktif);
                            dataProduk.child("harga").child(ConVar.HARGA_FIKTIF).setValue(null);
                        }

                        Log.d("DEBUG", "actioncompletedata sebelum upload gambar: " + actionCompleteData);
                        if(!actionCompleteData){
                            Log.d("DEBUG", "actionCompleteData di image upload bernilai false ");
                            Drawable drawable = inputProductIV.getDrawable();
                            if (drawable != null && drawable instanceof BitmapDrawable) {
                                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                                if (bitmap != null) {
                                    uploadGambarToStorage(namaProduk, barcode, dataProduk);
                                } else {
                                    Toast.makeText(ProductInputActivity.this, "Bitmap belum di-set", Toast.LENGTH_SHORT).show();
                                    submit.setEnabled(true);
                                    submit.setText("UPLOAD PRODUK");
                                    progressBar.setVisibility(View.GONE);
                                }
                            } else {
                                Toast.makeText(ProductInputActivity.this, "Input Gambar Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                                submit.setEnabled(true);
                                submit.setText("UPLOAD PRODUK");
                                progressBar.setVisibility(View.GONE);
                            }
                        } else{
                            Log.d("DEBUG", "actionCompleteData di image upload bernilai true ");
                            Toast.makeText(ProductInputActivity.this, "Data Berhasil Diupdate", Toast.LENGTH_SHORT).show();
                            submit.setEnabled(true);
                            submit.setText("UPLOAD PRODUK");
                            progressBar.setVisibility(View.GONE);
                        }

//                        Drawable drawable = inputProductIV.getDrawable();
//                        if (drawable != null && drawable instanceof BitmapDrawable) {
//                            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//                            if (bitmap != null) {
//                                uploadGambarToStorage(namaProduk, barcode, dataProduk);
//                            } else {
//                                Toast.makeText(ProductInputActivity.this, "Bitmap belum di-set", Toast.LENGTH_SHORT).show();
//                                submit.setEnabled(true);
//                                submit.setText("UPLOAD PRODUK");
//                                progressBar.setVisibility(View.GONE);
//                            }
//                        } else {
//                            Toast.makeText(ProductInputActivity.this, "Input Gambar Terlebih Dahulu", Toast.LENGTH_SHORT).show();
//                            submit.setEnabled(true);
//                            submit.setText("UPLOAD PRODUK");
//                            progressBar.setVisibility(View.GONE);
//                        }


                    }


                } else {
                    if (namaProduk.isEmpty() || merek.isEmpty() || kategori.isEmpty() || kemasan.isEmpty() || deskripsi.isEmpty()) {
                        Toast.makeText(ProductInputActivity.this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Jika semua field telah diisi, lanjutkan proses upload ke Firebase
                        // Kode untuk upload ke Firebase di sini
                        dataProduk.child("barcode").setValue(barcode);
                        dataProduk.child("nama").setValue(namaProduk);
                        dataProduk.child("merek").setValue(merek);
                        dataProduk.child("kategori").setValue(kategori);
                        dataProduk.child("kemasan").setValue(kemasan);
                        dataProduk.child("deskripsi").setValue(deskripsi);

                        if(adminToko.equals("Noari Indah")){
                            tersediaFiktif = false;
                            tersediaNoari = true;
                            dataProduk.child("ketersediaan").child(ConVar.KETERSEDIAAN_NOARI).setValue(tersediaNoari);
                            dataProduk.child("ketersediaan").child(ConVar.KETERSEDIAAN_FIKTIF).setValue(tersediaFiktif);
                            dataProduk.child("harga").child(ConVar.HARGA_NOARI).setValue(hargaProduk);
                            dataProduk.child("harga").child(ConVar.HARGA_FIKTIF).setValue(null);
                            return;
                        } else if (adminToko.equals("Fiktif Jaya")){
                            tersediaFiktif = true;
                            tersediaNoari = false;
                            dataProduk.child("ketersediaan").child(ConVar.KETERSEDIAAN_NOARI).setValue(tersediaNoari);
                            dataProduk.child("ketersediaan").child(ConVar.KETERSEDIAAN_FIKTIF).setValue(tersediaFiktif);
                            dataProduk.child("harga").child(ConVar.HARGA_FIKTIF).setValue(hargaProduk);
                            dataProduk.child("harga").child(ConVar.HARGA_NOARI).setValue(null);
                            return;
                        }

                        Log.d("DEBUG", "actioncompletedata sebelum upload gambar: " + actionCompleteData);
                        if(!actionCompleteData){
                            Log.d("DEBUG", "actionCompleteData di image upload bernilai false ");
                            Drawable drawable = inputProductIV.getDrawable();
                            if (drawable != null && drawable instanceof BitmapDrawable) {
                                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                                if (bitmap != null) {
                                    uploadGambarToStorage(namaProduk, barcode, dataProduk);
                                } else {
                                    Toast.makeText(ProductInputActivity.this, "Bitmap belum di-set", Toast.LENGTH_SHORT).show();
                                    submit.setEnabled(true);
                                    submit.setText("UPLOAD PRODUK");
                                    progressBar.setVisibility(View.GONE);
                                }
                            } else {
                                Toast.makeText(ProductInputActivity.this, "Input Gambar Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                                submit.setEnabled(true);
                                submit.setText("UPLOAD PRODUK");
                                progressBar.setVisibility(View.GONE);
                            }
                        } else{
                            Log.d("DEBUG", "actionCompleteData di image upload bernilai true ");
                            Toast.makeText(ProductInputActivity.this, "Data Berhasil Diupdate", Toast.LENGTH_SHORT).show();
                            submit.setEnabled(true);
                            submit.setText("UPLOAD PRODUK");
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                }

            }
        });
    }

    public void uploadGambarToStorage(String namaProduk, String barcode, DatabaseReference dataProduk){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String namaProdukNoSpasi = namaProduk.replace(" ", "-").toLowerCase();
        StorageReference imageRef = storageRef.child("ImgProduk/"+ namaProdukNoSpasi + "-" + barcode +".jpg");
        if (toFirebaseBitmap != null){
            Log.d("DEBUG", "onClick: to firebasebitmap not null");
        } else {
            Log.d("DEBUG", "onClick: to firebasebitmap null");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        toFirebaseBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUri = uri.toString();
                        dataProduk.child("gambar").setValue(imageUri);
                        Toast.makeText(ProductInputActivity.this, "Data Berhasil Diupload", Toast.LENGTH_SHORT).show();

                        submit.setEnabled(true);
                        submit.setText("UPLOAD PRODUK");
                        progressBar.setVisibility(View.GONE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1000);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Gagal mengupload Data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        submit.setEnabled(true);
                        submit.setText("UPLOAD PRODUK");
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Gagal mengupload gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                submit.setEnabled(true);
                submit.setText("UPLOAD PRODUK");
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void inputProductImage(){
        bottomSheetDialog = new BottomSheetDialog(ProductInputActivity.this);
        bottomSheetDialog.setContentView(R.layout.input_product_bottom_sheet);
        bottomSheetDialog.show();

        ivCamera = bottomSheetDialog.findViewById(R.id.ivCamera);
        ivGallery = bottomSheetDialog.findViewById(R.id.ivGallery);
        LinearLayout ivPreviewLayout = bottomSheetDialog.findViewById(R.id.ImageViewPreviewLayout);
        LinearLayout ivMetodeInputLayout = bottomSheetDialog.findViewById(R.id.MetodeImageViewLayout);

        imageViewPreview = bottomSheetDialog.findViewById(R.id.ImageViewPreview);
        imageViewDelete = bottomSheetDialog.findViewById(R.id.ImageViewDelete);

        if (capturedImage == null && galeriImage == null){
            InputGambarBottomSheet();
        } else {
            ivPreviewLayout.setVisibility(View.VISIBLE);
            ivMetodeInputLayout.setVisibility(View.GONE);
            Bitmap bitmap;
            Drawable drawable = inputProductIV.getDrawable();
            if (drawable instanceof BitmapDrawable){
                bitmap = ((BitmapDrawable) drawable).getBitmap();
                toFirebaseBitmap = ((BitmapDrawable) drawable).getBitmap();
                imageViewPreview.setImageBitmap(bitmap);
                long ukuranDalamBytes = bitmap.getByteCount();
                String ukuranFile = getUkuranDalamKBorMB(ukuranDalamBytes);
                TextView textViewInfo = bottomSheetDialog.findViewById(R.id.textViewInfo);
                String info = "Ukuran: " + ukuranFile + "\n"
                        + "Resolusi: " + bitmap.getWidth() + " x " + bitmap.getHeight();
                textViewInfo.setText(info);

                imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Hapus gambar dari ImageView dan set selectedBitmap menjadi null
                        imageViewPreview.setImageDrawable(null);
                        inputProductIV.setImageDrawable(null);
                        ivMetodeInputLayout.setVisibility(View.VISIBLE);
                        ivPreviewLayout.setVisibility(View.GONE);
                        capturedImage = null;
                        galeriImage = null;
                        InputGambarBottomSheet();
                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), "Belum ada gambar yang dipilih", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getUkuranDalamKBorMB(long ukuranDalamBytes){
        final long satuKB = 1024;
        final long satuMB = satuKB * satuKB;

        String ukuranFile;
        if (ukuranDalamBytes >= satuMB){
            float ukuranMB = (float) ukuranDalamBytes / satuMB;
            ukuranFile = String.format("%.2f MB", ukuranMB);
        } else {
            float ukuranKB = (float) ukuranDalamBytes / satuKB;
            ukuranFile = String.format("%.2f KB", ukuranKB);
        }

        return ukuranFile;
    }


    private void InputGambarBottomSheet(){
        // Tambahkan listener untuk memproses pilihan pengguna (kamera atau galeri)
        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tambahkan kode untuk menampilkan sumber gambar dari kamera
                bukaKamera();
                bottomSheetDialog.dismiss();
            }
        });

        ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tambahkan kode untuk menampilkan sumber gambar dari galeri
                bukaGaleri();
                bottomSheetDialog.dismiss();
            }
        });
    }

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    try {
                        // Mengambil gambar dari URI yang dipilih dan menampilkannya di ImageView
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        galeriImage = compressToSquare(bitmap);
                        inputProductIV.setImageBitmap(galeriImage);
                        toFirebaseBitmap = galeriImage;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    private void bukaGaleri() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getExtras() != null) {
                        // Ambil gambar dari intent
                        Bundle extras = data.getExtras();
                        Bitmap bitmapImage = (Bitmap) extras.get("data");

                        // Kompres gambar menjadi persegi dan tampilkan di ImageView
                        if (bitmapImage != null) {
                            capturedImage = compressToSquare(bitmapImage);
                            inputProductIV.setImageBitmap(capturedImage);
                            toFirebaseBitmap = capturedImage;
                        }
                    }
                }
            }
    );

    private void bukaKamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureLauncher.launch(takePictureIntent);
        }
    }

    private Bitmap compressToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Tentukan dimensi gambar persegi yang akan dihasilkan
        int dimension = Math.min(width, height);

        // Potong gambar menjadi persegi
        int x = (width - dimension) / 2;
        int y = (height - dimension) / 2;
        Bitmap squareBitmap = Bitmap.createBitmap(bitmap, x, y, dimension, dimension);
        Bitmap compressedBitmap = Bitmap.createScaledBitmap(squareBitmap, inputProductIV.getWidth(), inputProductIV.getHeight(), true);
        squareBitmap.recycle();

        return compressedBitmap;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Tombol kembali di ActionBar ditekan
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
//Akhir dari kode