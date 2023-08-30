package com.taufiqskripsiapp.masbro.Utils;

import android.app.Application;

import androidx.lifecycle.ViewModelProvider;

import com.taufiqskripsiapp.masbro.ViewModel.AppSharedViewModel;

public class MyDataManager extends Application {
    private AppSharedViewModel appSharedViewModel;

    public void onCreate(){
        super.onCreate();
        appSharedViewModel = new ViewModelProvider.AndroidViewModelFactory(this).create(AppSharedViewModel.class);
    }
    public AppSharedViewModel getAppSharedViewModel(){
        return appSharedViewModel;
    }
}
