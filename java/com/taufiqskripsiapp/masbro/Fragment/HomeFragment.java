package com.taufiqskripsiapp.masbro.Fragment;

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

import com.bumptech.glide.Glide;
import com.taufiqskripsiapp.masbro.R;
import com.taufiqskripsiapp.masbro.Utils.MasbroUtils;
import com.taufiqskripsiapp.masbro.ViewModel.HomeViewModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    AutoCompleteTextView autoCompleteText;
    TextView namaTokoTitle, alamatToko, jadwalToko;
    ImageView gambarToko;

    ArrayAdapter<String> adapterItems;
    HomeViewModel homeViewModel;
    ArrayList<String> listNamaToko = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
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
                String item = adapterView.getItemAtPosition(i).toString();
                homeViewModel.setPilihanToko(item);

                adapterItems = new ArrayAdapter<String>(requireContext(), R.layout.item_list, listNamaToko);
                autoCompleteText.setAdapter(adapterItems);
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