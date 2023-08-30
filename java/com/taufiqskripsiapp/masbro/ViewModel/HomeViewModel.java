package com.taufiqskripsiapp.masbro.ViewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> PilihanTokoLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> listNamaTokoLiveData = new MutableLiveData<>();
    private MutableLiveData<String> uriGambarTokoLiveData = new MutableLiveData<>();
    private MutableLiveData<String> alamatTokoLiveData = new MutableLiveData<>();
    private MutableLiveData<String> jadwalTokoLiveData = new MutableLiveData<>();
    ArrayList<String> listNamaToko = new ArrayList<>();
    private DayOfWeek currentDay;
    private String stringCurrentDay;

    public HomeViewModel(){

        currentDay = LocalDate.now().getDayOfWeek();
        stringCurrentDay = currentDay.getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
        stringCurrentDay = Character.toUpperCase(stringCurrentDay.charAt(0)) + stringCurrentDay.substring(1).toLowerCase();

        loadListNamaToko();
        Log.d("Test", "Nama Toko : " + PilihanTokoLiveData.getValue());
        InformasiToko();
    }

    private void InformasiToko(){
        String pilihanToko = PilihanTokoLiveData.getValue();
        if (pilihanToko == null){
            Log.d("Test", "pilihan Toko Tidak Ada Nilai");
        }
        DatabaseReference infoToko = FirebaseDatabase.getInstance().getReference("ShopApp/Toko/" + pilihanToko);
        infoToko.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String alamat = snapshot.child("Alamat").getValue(String.class);
                    String uriGambarToko = snapshot.child("Gambar").getValue(String.class);
                    alamatTokoLiveData.setValue(alamat);
                    uriGambarTokoLiveData.setValue(uriGambarToko);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference tokoDayRef = FirebaseDatabase.getInstance().getReference("ShopApp/Toko/" + pilihanToko + "/Jadwal/" + stringCurrentDay);
        tokoDayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String buka = snapshot.child("Buka").getValue(String.class);
                    String tutup = snapshot.child("Tutup").getValue(String.class);
                    jadwalTokoLiveData.setValue("Buka: " + buka + " - Tutup: " + tutup);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void loadListNamaToko() {
        DatabaseReference tokoRef = FirebaseDatabase.getInstance().getReference("ShopApp/Toko");
        tokoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listNamaToko.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    String namaToko = childSnapshot.getKey();
                    listNamaToko.add(namaToko);
                }
                listNamaTokoLiveData.setValue(listNamaToko);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public LiveData<ArrayList<String>> getListNamaToko(){
        return listNamaTokoLiveData;
    }

    public LiveData<String> getUriGambarTokoLiveData() {
        return uriGambarTokoLiveData;
    }

    public LiveData<String> getAlamatTokoLiveData() {
        return alamatTokoLiveData;
    }

    public LiveData<String> getJadwalTokoLiveData() {
        return jadwalTokoLiveData;
    }

    public void setPilihanToko(String toko){
        PilihanTokoLiveData.setValue(toko);
        InformasiToko();
    }
    public LiveData<String> getPilihanToko(){
        return PilihanTokoLiveData;
    }



}
