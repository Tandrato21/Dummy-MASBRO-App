package com.taufiqskripsiapp.masbro.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<String> userNameLiveData = new MutableLiveData<>();
    private MutableLiveData<String> adminTokoLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isAdminLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> superAdminLiveData = new MutableLiveData<>();

    private MutableLiveData<String> userUIDLiveData = new MutableLiveData<>();
    private boolean isUserDataFetched = false;

    public ProfileViewModel(){
        if(!isUserDataFetched){
            fetchUserDataFromDatabase();
            isUserDataFetched = true;
        }
    }



    public LiveData<String> getUserNameLiveData() {
        return userNameLiveData;
    }

    public LiveData<Boolean> getisAdminLiveData(){
        return isAdminLiveData;
    }
    public LiveData<String> getAdminTokoLiveData(){
        return adminTokoLiveData;
    }
    public LiveData<Boolean> getSuperAdminLiveData(){
        return superAdminLiveData;
    }
    public LiveData<String> getUserUIDLiveData(){
        return userUIDLiveData;
    }


    public void fetchUserDataFromDatabase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userUIDLiveData.setValue(uid);
        DatabaseReference userData = FirebaseDatabase.getInstance().getReference().child("ShopApp/Account/User").child(uid);
        DatabaseReference adminData = FirebaseDatabase.getInstance().getReference().child("ShopApp/Account/Admin").child(uid);

        userData.child("nama").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.getValue(String.class);
                if (userName != null) {
                    userNameLiveData.setValue(userName);

                    userData.child("isAdmin").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Boolean isAdmin = snapshot.getValue(Boolean.class);
                            if (isAdmin != null){
                                isAdminLiveData.setValue(isAdmin);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    // Jika nama tidak ditemukan di node User, coba ambil dari node Admin
                    adminData.child("nama").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String adminName = snapshot.getValue(String.class);
                            if (adminName != null) {
                                userNameLiveData.setValue(adminName);

                                adminData.child("isAdmin").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Boolean isAdmin = snapshot.getValue(Boolean.class);
                                        if (isAdmin != null){
                                            isAdminLiveData.setValue(isAdmin);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                adminData.child("adminToko").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String adminToko = snapshot.getValue(String.class);
                                        if(adminToko != null){
                                            adminTokoLiveData.setValue(adminToko);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                adminData.child("superAdmin").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Boolean superAdmin = snapshot.getValue(Boolean.class);
                                        if(superAdmin != null){
                                            superAdminLiveData.setValue(superAdmin);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error jika terjadi kesalahan
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error jika terjadi kesalahan
            }
        });
    }
}
