package com.taufiqskripsiapp.masbro.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taufiqskripsiapp.masbro.Adapter.Manager.ShoppingCartManager;
import com.taufiqskripsiapp.masbro.Adapter.ProductAdapter;
import com.taufiqskripsiapp.masbro.Adapter.Produk;
import com.taufiqskripsiapp.masbro.Adapter.ShoppingCartAdapter;
import com.taufiqskripsiapp.masbro.R;
import com.taufiqskripsiapp.masbro.Utils.ConVar;
import com.taufiqskripsiapp.masbro.Utils.MyDataManager;
import com.taufiqskripsiapp.masbro.ViewModel.AppSharedViewModel;
import com.taufiqskripsiapp.masbro.ViewModel.ProfileViewModel;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ShoppingFragment extends Fragment {

    AppSharedViewModel appSharedViewModel;
    ProfileViewModel profileViewModel;
    MyDataManager myDataManager;
    ShoppingCartAdapter shoppingCartAdapter;
    ShoppingCartManager shoppingCartManager;

    TextView textTotalHarga;

    RecyclerView recyclerView;
    MaterialToolbar topAppBar;

    List<Produk> produkList = new ArrayList<>();
    List<String> keyList = new ArrayList<>();
    SharedPreferences prefs;
    String uuid, tokoPilihan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        myDataManager = (MyDataManager) getActivity().getApplication();
        appSharedViewModel = myDataManager.getAppSharedViewModel();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState );
        shoppingCartManager = new ShoppingCartManager();

        prefs = requireActivity().getSharedPreferences("MASBRO_App", MODE_PRIVATE);
        uuid = prefs.getString("currentUUID", null);
        String status = prefs.getString("currentStatus", "incomplete");
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("currentUUID", uuid);
            editor.apply();
        }

        shoppingCartManager.setOnItemButtonClickListener(new ShoppingCartManager.OnItemButtonClickListener() {
            @Override
            public void onIncrementClick(int position) {
                Produk produk = produkList.get(position);

                // Dapatkan ViewHolder dan perbarui TextView
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder instanceof ShoppingCartAdapter.ShoppingViewHolder) {
                    ShoppingCartAdapter.ShoppingViewHolder shoppingViewHolder = (ShoppingCartAdapter.ShoppingViewHolder) viewHolder;
                    int jumlahSaatIni = Integer.parseInt(shoppingViewHolder.textJumlahProduk.getText().toString());
                    jumlahSaatIni++;
                    shoppingViewHolder.textJumlahProduk.setText(String.valueOf(jumlahSaatIni));
                    Log.d("DEBUG", "hargaPerUnit: " + shoppingViewHolder.hargaPerUnit + ", jumlahSaatIni: " + jumlahSaatIni);

                    int hargaTotal = shoppingViewHolder.hargaPerUnit * jumlahSaatIni;
                    appSharedViewModel.setHargaTotal(position, hargaTotal);
                    appSharedViewModel.setJumlahProdukMap(produk.getKey(), jumlahSaatIni);
                    appSharedViewModel.hitungTotalHargaKeseluruhan();

                }
            }

            @Override
            public void onDecrementClick(int position) {
                Produk produk = produkList.get(position);

                // Sama seperti di atas, tapi untuk decrement
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder instanceof ShoppingCartAdapter.ShoppingViewHolder) {
                    ShoppingCartAdapter.ShoppingViewHolder shoppingViewHolder = (ShoppingCartAdapter.ShoppingViewHolder) viewHolder;
                    int jumlahSaatIni = Integer.parseInt(shoppingViewHolder.textJumlahProduk.getText().toString());
                    if (jumlahSaatIni > 1) {
                        jumlahSaatIni--;
                        shoppingViewHolder.textJumlahProduk.setText(String.valueOf(jumlahSaatIni));

                        int hargaTotal = shoppingViewHolder.hargaPerUnit * jumlahSaatIni;
                        appSharedViewModel.setHargaTotal(position, hargaTotal);
                        appSharedViewModel.setJumlahProdukMap(produk.getKey(), jumlahSaatIni);
                        appSharedViewModel.hitungTotalHargaKeseluruhan();
                    }
                }
            }
        });

        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.DeleteAll) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Konfirmasi Hapus")
                            .setMessage("Apakah Anda yakin ingin menghapus semua item?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    produkList.clear();
                                    keyList.clear();
                                    appSharedViewModel.clearData();


                                    shoppingCartAdapter.notifyDataSetChanged();

                                    // Jika menggunakan ViewModel, jangan lupa untuk mengupdate data di ViewModel juga
                                    // contoh: appSharedViewModel.clearAllData();


                                }
                            })
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                    return true;
                }
                if(id == R.id.Save){
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Konfirmasi Hapus")
                            .setMessage("Apakah Anda ingin menyimpan atau menyelesaikan belanja?")
                            .setPositiveButton("Simpan Saja", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    HashMap<String, Integer> jumlahProdukMap = appSharedViewModel.getJumlahProdukMap().getValue();
//                                    saveToDatabase(uuid, "incomplete");
                                    if (jumlahProdukMap != null) {
                                        saveToDatabase(uuid, "incomplete", jumlahProdukMap);
                                    }
                                }
                            })
                            .setNegativeButton("Simpan dan Selesai", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    HashMap<String, Integer> jumlahProdukMap = appSharedViewModel.getJumlahProdukMap().getValue();
//                                    saveToDatabase(uuid, "complete");
//                                    resetUUIDAndCart();
                                    if (jumlahProdukMap != null) {
                                        saveToDatabase(uuid, "complete", jumlahProdukMap);
                                        resetUUIDAndCart();
                                    }
                                }
                            })
                            .show();
                    return true;
                }
                return false;
            }
        });


        shoppingCartAdapter = new ShoppingCartAdapter(produkList, shoppingCartManager, getViewLifecycleOwner(), appSharedViewModel);
        recyclerView.setAdapter(shoppingCartAdapter);

        appSharedViewModel.getKeyProdukHasilScanLiveData().observe(getViewLifecycleOwner(), keyHasilScan -> {
            if(keyHasilScan != null){
                keyList.addAll(keyHasilScan);
                UpdateRecycveView();
            }
        });

        appSharedViewModel.getJumlahProdukMap().observe(getViewLifecycleOwner(), jumlahProdukMap -> {
            if(jumlahProdukMap != null){
                shoppingCartAdapter.updateJumlahProduk(jumlahProdukMap);
            }
        });
        // Di dalam onViewCreated
        appSharedViewModel.getTotalHargaKeseluruhan().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer totalHarga) {
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
                String hargaFormated = numberFormat.format(totalHarga).replace("Rp", "Rp. ");
                textTotalHarga.setText(hargaFormated);
            }
        });

        appSharedViewModel.getPilihanTokoLiveData().observe(getViewLifecycleOwner(), pilihanToko -> {
            tokoPilihan = pilihanToko;
        });



    }

    public void UpdateRecycveView(){
        produkList.clear();
        for (String keyProduk : keyList){
            DatabaseReference produkRef = FirebaseDatabase.getInstance().getReference(ConVar.PRODUK_DB_REF).child(keyProduk);
            produkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Produk produk = snapshot.getValue(Produk.class);
                    if (produk != null){
                        produk.setKey(keyProduk);
                        produkList.add(produk);
                    }

                    shoppingCartAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        if(produkList != null){
            appSharedViewModel.hitungTotalHargaKeseluruhan();
        }

    }

    public void saveToDatabase(String uuid, String status, HashMap<String, Integer> jumlahProdukMap) {
        // Simpan data ke database
        // ...
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference(ConVar.HISTORY_DB_REF);
        String uidUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("UUID", uuid);
        dataMap.put("Status", status);
        dataMap.put("TanggalWaktu", timeStamp);
        dataMap.put("KeyList", keyList);
        dataMap.put("JumlahProduk", jumlahProdukMap);

        historyRef.child(uidUser).child(tokoPilihan).child(uuid).setValue(dataMap);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("currentStatus", status);
        editor.apply();
    }

    // Fungsi untuk mereset UUID dan keranjang belanja
    public void resetUUIDAndCart() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("currentUUID");
        editor.remove("currentStatus");
        editor.apply();
        // Kosongkan keranjang belanja
        produkList.clear();
        keyList.clear();
        appSharedViewModel.clearData();
        shoppingCartAdapter.notifyDataSetChanged();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_shopping, container, false);
        recyclerView = view.findViewById(R.id.RecycleViewShopping);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        textTotalHarga = view.findViewById(R.id.TextTotalHarga);
        setHasOptionsMenu(true);
        topAppBar = view.findViewById(R.id.topAppBar);





        return view;
    }
}