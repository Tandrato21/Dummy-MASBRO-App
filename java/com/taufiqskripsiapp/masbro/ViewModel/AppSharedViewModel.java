package com.taufiqskripsiapp.masbro.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AppSharedViewModel extends ViewModel {
    private final MutableLiveData<String> scannedBarcodeLiveData = new MutableLiveData<>();


    public void setBarcodeScanValue(String barcode){
        Log.d("TAG SVM", "setBarcodeScanValue: "+ barcode);
        scannedBarcodeLiveData.setValue(barcode);
        String barcodeLiveData = scannedBarcodeLiveData.getValue();
        Log.d("TAG SVM", "setBarcodeScanValue live data: " + barcodeLiveData);

    }

    public LiveData<String> getBarcodeScanValue(){
        Log.d("TAG SVM", "getBarcodeScanValue: " + scannedBarcodeLiveData);
        String barcodeLiveData = scannedBarcodeLiveData.getValue();
        Log.d("TAG SVM", "setBarcodeScanValue live data: " + barcodeLiveData);
        return scannedBarcodeLiveData;
    }
}
