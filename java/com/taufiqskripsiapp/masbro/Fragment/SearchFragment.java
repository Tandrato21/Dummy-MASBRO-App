package com.taufiqskripsiapp.masbro.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.taufiqskripsiapp.masbro.Adapter.ProductAdapter;
import com.taufiqskripsiapp.masbro.Adapter.Produk;
import com.taufiqskripsiapp.masbro.CaptureAct;
import com.taufiqskripsiapp.masbro.Fragment.SearchFrag.ProductInputActivity;
import com.taufiqskripsiapp.masbro.R;
import com.taufiqskripsiapp.masbro.Utils.BoyerMooreSearch;
import com.taufiqskripsiapp.masbro.Utils.MasbroUtils;
import com.taufiqskripsiapp.masbro.Utils.MyDataManager;
import com.taufiqskripsiapp.masbro.ViewModel.AppSharedViewModel;
import com.taufiqskripsiapp.masbro.ViewModel.ProfileViewModel;
import com.taufiqskripsiapp.masbro.ViewModel.SharedViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    FloatingActionButton addItemFAB;

    TextInputLayout dialogBarcodeLayout, dialogHargaLayout;
    TextInputEditText dialogBarcodeEditText, dialogHargaEditText;

    SearchView searchView;
    SearchBar searchBar;

    SharedViewModel viewModel;
    MyDataManager myDataManager;
    AppSharedViewModel appSharedViewModel;
    ProfileViewModel profileViewModel;
    Boolean isAdmin, superAdmin;
    String adminToko;

    List<String> allKeys;
    BoyerMooreSearch boyerMooreSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        myDataManager = (MyDataManager) getActivity().getApplication();
        appSharedViewModel = myDataManager.getAppSharedViewModel();

    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MasbroUtils.hideKeyboard(requireContext(), getView());
                return false;
            }
        });

        DatabaseReference produkRef = FirebaseDatabase.getInstance().getReference("ShopApp/Produk");
        List<Produk> produkList = new ArrayList<>();
        ProductAdapter productAdapter = new ProductAdapter(produkList);
        recyclerView.setAdapter(productAdapter);

        produkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produkList.clear();
                for (DataSnapshot produkSnapshot : snapshot.getChildren()){
                    Produk produk = produkSnapshot.getValue(Produk.class);
                    produkList.add(produk);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        allKeys = new ArrayList<>();
        produkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot produkSnapshot : snapshot.getChildren()){
                    String key = produkSnapshot.getKey();  // Ini adalah gabungan dari barcode dan nama produk
                    Log.d("DEBUG", "onDataChange: key = " + key);
                    allKeys.add(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });


        profileViewModel.getisAdminLiveData().observe(getViewLifecycleOwner(), admin -> {
            if (admin == null) {
                Log.d("TAG", "onViewCreated: admin dari isAdminLiveData bernilai null");
            }
            isAdmin = admin;
            Log.d("TAG", "onViewCreated: is admin bernilai "+ isAdmin);
            if (isAdmin.equals(false)){
                addItemFAB.setVisibility(View.GONE);
            } else {
                addItemFAB.setVisibility(View.VISIBLE);
                profileViewModel.getSuperAdminLiveData().observe(getViewLifecycleOwner(), adminSuper ->{
                    superAdmin = adminSuper;
                    profileViewModel.getAdminTokoLiveData().observe(getViewLifecycleOwner(), tokoAdmin ->{
                        adminToko = tokoAdmin;
                    });
                });
            }
        });
//


        addItemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showScanProdukDialog();
            }
        });

    }

    private void showScanProdukDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog_layout_inputbarcode, null);
        dialogBarcodeEditText = view.findViewById(R.id.ETProdukBarcode);
        dialogBarcodeLayout = view.findViewById(R.id.ETLayoutProdukBarcode);

        dialogBarcodeLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanBarcode();
            }
        });


        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Input Produk Baru")
                .setMessage("Scan atau Ketik Manual Barcode Produk Yang Ingin Ditambahkan")
                .setView(view)

                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Konfirmasi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String manualBarcode = dialogBarcodeEditText.getText().toString();
                        appSharedViewModel.setBarcodeScanValue(manualBarcode);

                        String barcodeToSearch = dialogBarcodeEditText.getText().toString();
                        boyerMooreSearch = new BoyerMooreSearch(barcodeToSearch);
                        String keyCocok = null;
                        Log.d("DEBUG", "onClick: nilai barcode to search adalah " + barcodeToSearch);
                        Boolean isBarcodeFound = false;

                        for(String key : allKeys){
                            int position = boyerMooreSearch.search(key);
                            if(position != key.length()){
                                isBarcodeFound = true;
                                keyCocok = key;
                                break;

                            }
                        }

                        if(isBarcodeFound) {
                            DialogBarcodeFoundAction(keyCocok);


                        } else {
                            Intent intent = new Intent(getActivity(), ProductInputActivity.class);
                            startActivity(intent);
                        }

                    }
                })
                .show();


    }

    public void DialogBarcodeFoundAction(String keyCocok){
        DatabaseReference produkRef = FirebaseDatabase.getInstance().getReference("ShopApp/Produk").child(keyCocok);
        produkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer hargaFiktif = snapshot.child("harga").child("tokoFiktif").getValue(Integer.class);
                Integer hargaNoari = snapshot.child("harga").child("tokoNoari").getValue(Integer.class);
                Boolean ketersediaanFiktif = snapshot.child("ketersediaan").child("tokoFiktif").getValue(Boolean.class);
                Boolean ketersediaanNoari = snapshot.child("ketersediaan").child("tokoNoari").getValue(Boolean.class);

                String namaProduk = snapshot.child("nama").getValue(String.class);
                String merek = snapshot.child("merek").getValue(String.class);
                String barcode = snapshot.child("barcode").getValue(String.class);
                String kemasan = snapshot.child("kemasan").getValue(String.class);
                String kategori = snapshot.child("kategori").getValue(String.class);
                String uri = snapshot.child("gambar").getValue(String.class);

                if(superAdmin){
                    if(hargaFiktif == null || !ketersediaanFiktif){
                        barcodeDetectDataProdukInputDialog(keyCocok, namaProduk, merek, barcode, kemasan, kategori, uri);
                    } else if (hargaFiktif != null || ketersediaanFiktif){
                        Toast.makeText(getContext(), "Data Sudah Ada", Toast.LENGTH_SHORT).show();
                    } else if (hargaNoari == null || !ketersediaanNoari){
                        barcodeDetectDataProdukInputDialog(keyCocok, namaProduk, merek, barcode, kemasan, kategori, uri);
                    } else {
                        Toast.makeText(getContext(), "Data Sudah Ada", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (adminToko.equals("Fiktif Jaya")){
                        if(hargaFiktif == null || !ketersediaanFiktif){
                            barcodeDetectDataProdukInputDialog(keyCocok, namaProduk, merek, barcode, kemasan, kategori, uri);
                        } else {
                            Toast.makeText(getContext(), "Data Sudah Ada", Toast.LENGTH_SHORT).show();
                        }


                    } else if (adminToko.equals("Noari Indah")) {
                        if(hargaNoari == null || !ketersediaanNoari){
                            barcodeDetectDataProdukInputDialog(keyCocok, namaProduk, merek, barcode, kemasan, kategori, uri);
                        } else{
                            Toast.makeText(getContext(), "Data Sudah Ada", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void barcodeDetectDataProdukInputDialog(String keyCocok, String nama, String merek, String barcode, String kemasan, String kategori, String uri){
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog_layout_inputbarcode_updatedata, null);
        dialogHargaEditText = view.findViewById(R.id.ETProdukHarga);
        dialogHargaLayout = view.findViewById(R.id.ETLayoutProdukHarga);
        TextView namaProdukText = view.findViewById(R.id.TextNamaProduk);
        TextView barcodeText = view.findViewById(R.id.TextBarcode);
        TextView merekText = view.findViewById(R.id.TextMerek);
        TextView kemasanText = view.findViewById(R.id.TextKemasan);
        TextView kategoriText = view.findViewById(R.id.TextKategori);
        ImageView produkGambar = view.findViewById(R.id.IVGambarProduk);
        namaProdukText.setText(nama);
        barcodeText.setText(barcode);
        merekText.setText(merek);
        kemasanText.setText(kemasan);
        kategoriText.setText(kategori);
        Glide.with(requireContext())
                .load(uri)
                .into(produkGambar);

        DatabaseReference produkRef = FirebaseDatabase.getInstance().getReference("ShopApp/Produk").child(keyCocok);


        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Produk Ditemukan Di Database")
                .setMessage("Data belum lengkap. Silahkan dilengkapi")
                .setView(view)
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Perbarui Data", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String harga = dialogHargaEditText.getText().toString();
                        if(adminToko != null){
                            Log.d("DEBUG", "onClick: adminToko " + adminToko);
                        } else{
                            Log.d("DEBUG", "onClick: adminToko null");
                        }


                        if (adminToko.equals("Fiktif Jaya")){
                            Integer hargaProduk= Integer.parseInt(harga);
                            Boolean tersediaFiktif = true;
                            produkRef.child("harga").child("tokoFiktif").setValue(hargaProduk);
                            produkRef.child("ketersediaan").child("tokoFIktif").setValue(tersediaFiktif).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "Harga Berhasil Diupdate", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (adminToko.equals("Noari Indah")) {
                            Integer hargaProduk= Integer.parseInt(harga);
                            Boolean tersediaNoari = true;
                            produkRef.child("harga").child("tokoNoari").setValue(hargaProduk);
                            produkRef.child("ketersediaan").child("tokoNoari").setValue(tersediaNoari).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "Harga Berhasil Diupdate", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                })
                .show();
    }

    private void scanBarcode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Arahkan kamera ke barcode produk");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null ){
            String scannedBarcode = result.getContents();
            appSharedViewModel.setBarcodeScanValue(scannedBarcode);
            appSharedViewModel.getBarcodeScanValue().observe(getViewLifecycleOwner(), barcode ->{
                if(barcode != null){
                    dialogBarcodeEditText.setText(barcode);
                }
            });
//            viewModel.getBarcodeScanValue().observe(this, new Observer<String>() {
//                @Override
//                public void onChanged(String scannedBarcode) {
//                    dialogBarcodeEditText.setText(scannedBarcode);
//                }
//            });
        }
    });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

//        TODO: Aktifkan jika ingin mencoba Recycleview
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchView =  rootView.findViewById(R.id.SearchView);
        searchBar = rootView.findViewById(R.id.search_bar);
        searchView.setupWithSearchBar(searchBar);
        addItemFAB = rootView.findViewById(R.id.AddProductFab);


        return rootView;
    }
}
