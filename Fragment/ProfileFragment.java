package com.taufiqskripsiapp.masbro.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.taufiqskripsiapp.masbro.LoginActivity;
import com.taufiqskripsiapp.masbro.R;
import com.taufiqskripsiapp.masbro.ViewModel.ProfileViewModel;

public class ProfileFragment extends Fragment{

    TextView namaUser, isAdminText, adminTokoText;
    Button logoutButton;
    private ProfileViewModel profileViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: gunakan "requiredActivity()" dan bukannya "this".
        // Ditujukan untuk MainActivity.java dan bukannya fragment ini.

        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);


    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        logoutButton = view.findViewById(R.id.LogoutButton);
        namaUser = view.findViewById(R.id.NamaUser);
        isAdminText = view.findViewById(R.id.isAdmin);
        adminTokoText = view.findViewById(R.id.adminToko);

        profileViewModel.getUserNameLiveData().observe(getViewLifecycleOwner(), userName -> {
            namaUser.setText(userName);
        });

        profileViewModel.getisAdminLiveData().observe(getViewLifecycleOwner(), isAdmin -> {
            if (isAdmin.equals(true)){
                isAdminText.setText("ADMIN");
                profileViewModel.getSuperAdminLiveData().observe(getViewLifecycleOwner(), adminSuper ->{
                    if(adminSuper.equals(true)){
                        adminTokoText.setVisibility(View.GONE);
                    }
                });
                profileViewModel.getAdminTokoLiveData().observe(getViewLifecycleOwner(), adminToko -> {
                        adminTokoText.setText(adminToko);
                });

            } else {
                isAdminText.setText("USER");
                adminTokoText.setVisibility(View.GONE);
            }
        });




        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireActivity().getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        return view;
    }

}