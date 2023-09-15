//Catatan : Ini Bukan Komentar Biasa, Ini Adalah Penanda. Ini Kode HomeFragment.java
package com.taufiqskripsiapp.masbro.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.taufiqskripsiapp.masbro.R;
import com.taufiqskripsiapp.masbro.Utils.MasbroUtils;
import com.taufiqskripsiapp.masbro.Utils.MyDataManager;
import com.taufiqskripsiapp.masbro.ViewModel.AppSharedViewModel;
import com.taufiqskripsiapp.masbro.ViewModel.HomeViewModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    AutoCompleteTextView autoCompleteText;
    TextView namaTokoTitle, alamatToko, jadwalToko;
    ImageView gambarToko;

    ArrayAdapter<String> adapterItems;
    HomeViewModel homeViewModel;
    AppSharedViewModel appSharedViewModel;
    MyDataManager myDataManager;
    ArrayList<String> listNamaToko = new ArrayList<>();
    Boolean isSecondClick = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        myDataManager = (MyDataManager) getActivity().getApplication();
        appSharedViewModel = myDataManager.getAppSharedViewModel();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MasbroUtils.hideKeyboard(requireContext(), getView());
                return false;
            }
        });

        homeViewModel.getPilihanToko().observe(getViewLifecycleOwner(), storeName -> {
            if (storeName != null) {
                namaTokoTitle.setText(storeName);
                autoCompleteText.setText(storeName);
                appSharedViewModel.setPilihanTokoLiveData(storeName);
            }
        });
        homeViewModel.getAlamatTokoLiveData().observe(getViewLifecycleOwner(), alamat -> {
            if (alamat != null){
                alamatToko.setText(alamat);
            }
        });

        homeViewModel.getJadwalTokoLiveData().observe(getViewLifecycleOwner(), jadwal -> {
            if (jadwal != null) {
                jadwalToko.setText(jadwal);
            }
        });

        homeViewModel.getUriGambarTokoLiveData().observe(getViewLifecycleOwner(), gambar -> {
            if (gambar != null){
                Glide.with(requireContext())
                        .load(gambar)
                        .into(gambarToko);
            }
        });


        homeViewModel.getListNamaToko().observe(getViewLifecycleOwner(), list ->{
            if (listNamaToko != null){
                listNamaToko.clear();
                listNamaToko.addAll(list);

                adapterItems = new ArrayAdapter<String>(requireContext(), R.layout.item_list, listNamaToko);
                autoCompleteText.setAdapter(adapterItems);
            }

        });

        autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String item = adapterView.getItemAtPosition(i).toString(); // Simpan item yang dipilih
                if(isSecondClick){
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Peringatan")
                            .setMessage("Mengubah toko akan mempengaruhi Shopping Cart Anda. Apakah Anda yakin?")
                            .setPositiveButton("Ya, Lanjutkan", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    String item = adapterView.getItemAtPosition(i).toString();
                                    homeViewModel.setPilihanToko(item);
                                    Toast.makeText(getContext(), "Toko yang dipilih : " + item, Toast.LENGTH_SHORT).show();

                                    adapterItems = new ArrayAdapter<String>(requireContext(), R.layout.item_list, listNamaToko);
                                    autoCompleteText.setAdapter(adapterItems);
                                }
                            })
                            .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    homeViewModel.getPilihanToko().observe(getViewLifecycleOwner(), storeName -> {
                                        if (storeName != null) {
                                            namaTokoTitle.setText(storeName);
                                            autoCompleteText.setText(storeName);
                                            appSharedViewModel.setPilihanTokoLiveData(storeName);
                                            Toast.makeText(getContext(), "Toko yang dipilih : " + storeName, Toast.LENGTH_SHORT).show();
                                            adapterItems = new ArrayAdapter<String>(requireContext(), R.layout.item_list, listNamaToko);
                                            autoCompleteText.setAdapter(adapterItems);
                                        }
                                    });
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                } else {
//                    String item = adapterView.getItemAtPosition(i).toString();
                    homeViewModel.setPilihanToko(item);
                    Toast.makeText(getContext(), "Toko yang dipilih : " + item, Toast.LENGTH_SHORT).show();

                    adapterItems = new ArrayAdapter<String>(requireContext(), R.layout.item_list, listNamaToko);
                    autoCompleteText.setAdapter(adapterItems);
                    isSecondClick = true;
                }


            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        autoCompleteText = rootView.findViewById(R.id.AutoCompleteText);

        namaTokoTitle = rootView.findViewById(R.id.NamaTokoTitle);
        alamatToko = rootView.findViewById(R.id.AlamatToko);
        gambarToko = rootView.findViewById(R.id.GambarToko);
        jadwalToko = rootView.findViewById(R.id.JadwalToko);

        return rootView;
    }
}
//Bagian Akhir