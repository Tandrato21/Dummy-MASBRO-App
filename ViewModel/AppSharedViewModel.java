package com.taufiqskripsiapp.masbro.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AppSharedViewModel extends ViewModel {
    private final MutableLiveData<String> scannedBarcodeLiveData = new MutableLiveData<>();
    private final MutableLiveData<HashMap<String, Object>> produkDataMapLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> pilihanTokoLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> keyProdukHasilScanLiveData = new MutableLiveData<>(new ArrayList<>());
//    private final MutableLiveData<Integer> hargaTotalLiveData = new MutableLiveData<>();
    private final HashMap<Integer,  MutableLiveData<Integer>> hargaTotalMap = new HashMap<>(0);
    private final MutableLiveData<HashMap<String, Integer>> jumlahProdukMap = new MutableLiveData<>(new HashMap<>());
    private MutableLiveData<Integer> totalHargaKeseluruhan = new MutableLiveData<>();

    public void clearData() {
        keyProdukHasilScanLiveData.setValue(new ArrayList<>());
        jumlahProdukMap.setValue(new HashMap<>());
    }

    public void setBarcodeScanValue(String barcode){
        scannedBarcodeLiveData.setValue(barcode);

    }
    public LiveData<String> getBarcodeScanValue(){
        return scannedBarcodeLiveData;
    }

//    public void setProdukDataMap(String key, Object value){
//        HashMap<String, Object> tempMap = produkDataMapLiveData.getValue();
//        if(tempMap == null){
//            tempMap = new HashMap<>();
//        }
//        tempMap.put(key, value);
//        produkDataMapLiveData.setValue(tempMap);
//        if (tempMap.containsKey("namaProduk") && tempMap.containsKey("merek")) {
//            Log.d("DEBUG", "namaProduk and merek are set: " + tempMap.get("namaProduk") + ", " + tempMap.get("merek"));
//        } else {
//            Log.d("DEBUG", "namaProduk and/or merek are not set");
//        }
//    }
    public void setProdukDataMap(HashMap<String, Object> dataMap){
        HashMap<String, Object> tempMap = produkDataMapLiveData.getValue();
        if(tempMap == null){
            tempMap = new HashMap<>();
        }
        tempMap.putAll(dataMap);
        produkDataMapLiveData.setValue(tempMap);
    }


    public LiveData<HashMap<String, Object>> getProdukDataMap(){
        return  produkDataMapLiveData;
    }

    public  void setPilihanTokoLiveData(String namaToko){
        if(namaToko != null){
            pilihanTokoLiveData.setValue(namaToko);
        }

    }
    public LiveData<String> getPilihanTokoLiveData(){
        if(pilihanTokoLiveData == null){
        } else {
        }
        return  pilihanTokoLiveData;
    }

    public void setKeyProdukHasilScanLiveData(String newKey){
        List<String> existingKeys = keyProdukHasilScanLiveData.getValue();
        if (existingKeys == null) {
            existingKeys = new ArrayList<>();
        }
        existingKeys.add(newKey);
        keyProdukHasilScanLiveData.setValue(existingKeys);
    }
    public LiveData<List<String>> getKeyProdukHasilScanLiveData(){
        return  keyProdukHasilScanLiveData;
    }

//    public void  setHargaTotalLiveData(int hargaTotal){
//        hargaTotalLiveData.setValue(hargaTotal);
//    }
//
//    public LiveData<Integer> getHargaTotalLiveData(){
//        return hargaTotalLiveData;
//    }
    public LiveData<Integer> getHargaTotalLiveData(int position) {
        if (!hargaTotalMap.containsKey(position)) {
            hargaTotalMap.put(position, new MutableLiveData<>());
        }
        return hargaTotalMap.get(position);
    }

    public void setHargaTotal(int position, int hargaTotal) {
        if (!hargaTotalMap.containsKey(position)) {
            hargaTotalMap.put(position, new MutableLiveData<>());
        }
        Log.d("DEBUG", "Setting harga total for position: " + position + " with value: " + hargaTotal);
        hargaTotalMap.get(position).setValue(hargaTotal);
    }
    public HashMap<Integer, MutableLiveData<Integer>> getHargaTotalMap() {
        return hargaTotalMap;
    }


    public void  setJumlahProdukMap(String key, int jumlah){
        HashMap<String, Integer> tempMap = jumlahProdukMap.getValue();
        if (tempMap == null) {
            tempMap = new HashMap<>();
        }
        tempMap.put(key, jumlah);
        jumlahProdukMap.setValue(tempMap);
    }
    public LiveData<HashMap<String, Integer>> getJumlahProdukMap() {
        return jumlahProdukMap;
    }

    public void hitungTotalHargaKeseluruhan() {
        HashMap<Integer, MutableLiveData<Integer>> hargaTotalMap = getHargaTotalMap();
        int total = 0;
        for (MutableLiveData<Integer> liveData : hargaTotalMap.values()) {
            Integer value = liveData.getValue();
            if (value != null) {
                total += value;
            }
        }
//        totalHargaKeseluruhan.setValue(total);
        Log.d("DEBUG", "Total harga sebelum di-set: " + total);
        totalHargaKeseluruhan.setValue(total);
        Log.d("DEBUG", "Total harga setelah di-set: " + totalHargaKeseluruhan.getValue());
    }


    public MutableLiveData<Integer> getTotalHargaKeseluruhan() {
        return totalHargaKeseluruhan;
    }
}
