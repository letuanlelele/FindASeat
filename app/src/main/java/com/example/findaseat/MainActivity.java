package com.example.findaseat;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.findaseat.databinding.ActivityMainBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.FirebaseApp;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;    // nav_menu
    private static boolean isLoggedIn;
    private static String username;

    public static void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public static void setUsername(String user_name){
        username = user_name;
    }

    public static String getUsername(){
        return username;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // nav_menu
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new MapFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_map) {
                replaceFragment(new MapFragment());
            } else if (itemId == R.id.navigation_profile) {
                if (isLoggedIn) {
                    replaceFragment(new ProfileFragment());
                } else {
                    replaceFragment(new LoginFragment());
                }
            }
            return true;
        });

        // Firebase
        FirebaseApp.initializeApp(this);
    }

    // nav_menu
    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap) {
//
//    }
}


