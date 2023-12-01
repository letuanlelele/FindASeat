package com.example.findaseat;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Map<String, BuildingInfo> buildingInfoMap = new HashMap<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Access BuildingInfo collection
    private CollectionReference BuildingInfo = db.collection("BuildingInfo");


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    // Google map
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    // Used for importing Google Map
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Set the bounds to confine the map to the campus
        LatLngBounds campusBounds = new LatLngBounds(
                new LatLng(34.019043067462896, -118.29109258963628),       // Bottom left corner

                new LatLng(34.025442742634105, -118.28045999644873)        // Top right corner
        );
//        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(campusBounds, 100));
        if (!TestUtils.isRunningTest()) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(campusBounds, 100));
        }
        // Create buildings + Add markers for buildings and set click listeners
        hardCodeBuildings();
    }




    private void startFireStore() {
        BuildingInfo
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Populate buildingInfoMap
//                                BuildingInfo building = new BuildingInfo();
//                                String ID = document.getId();
//                                building.setDocument_id(ID);
//                                building.setBuilding_id(document.get("building_id").toString());
                                String test = document.get("building_id").toString();
                                Toast.makeText(getActivity(), test, Toast.LENGTH_SHORT).show();
//                                building.setDescription(document.getString("description"));
//                                building.setLatitude(document.getDouble("latitude"));
//                                building.setLongitude(document.getDouble("longitude"));

//                                buildingInfoMap.put(building.getBuilding_id(), building);
//                                if (!buildingInfoMap.isEmpty()) {
//                                    Toast.makeText(getActivity(), "not empty", Toast.LENGTH_SHORT).show();
//                                }


//                                LatLng position = new LatLng(building.getLatitude(), building.getLongitude());
//                                mMap.addMarker(new MarkerOptions().position(position).title(building.getBuilding_id()));

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            Toast.makeText(getActivity(), "onComplete sucess", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "onComplete NOT sucess: ", task.getException());
                            Toast.makeText(getActivity(), "onComplete NOT sucess", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void hardCodeBuildings() {
        BuildingInfo building1 = new BuildingInfo();
        building1.setBuilding_id("Leavey");
        building1.setDescription("Leavey is...");
        building1.setLatitude(34.02204048561001);
        building1.setLongitude(-118.28292515212222);
        building1.setOpeningTime(830);
        building1.setClosingTime(2300);
        building1.setNum_seats(10);
        building1.setSeatLocations(new boolean[]{ true, true, false, false, false, true, true, true, true, false });
        // Put building in hashmap
        buildingInfoMap.put(building1.getBuilding_id(), building1);


        BuildingInfo building2 = new BuildingInfo();
        building2.setBuilding_id("Fertitta");
        building2.setDescription("Fertitta is...");
        building2.setLatitude(34.01940128537009);
        building2.setLongitude(-118.28240971952378);
        building2.setOpeningTime(1000);
        building2.setClosingTime(1200);
        building2.setNum_seats(5);
        building2.setSeatLocations(new boolean[]{ false, false, false, false, false, true, true, true, true, false });
        // Put building in hashmap
        buildingInfoMap.put(building2.getBuilding_id(), building2);


        BuildingInfo building3 = new BuildingInfo();
        building3.setBuilding_id("Doheny");
        building3.setDescription("Doheny is...");
        building3.setLatitude(34.02098157202499);
        building3.setLongitude(-118.28390788352026);
        building3.setOpeningTime(1000);
        building3.setClosingTime(1900);
        building3.setNum_seats(10);
        building3.setSeatLocations(new boolean[]{ true, false, false, false, false, true, true, true, true, false });
        // Put building in hashmap
        buildingInfoMap.put(building3.getBuilding_id(), building3);


        BuildingInfo building4 = new BuildingInfo();
        building4.setBuilding_id("Hoose");
        building4.setDescription("Hoose is...");
        building4.setLatitude(34.01965126926076);
        building4.setLongitude(-118.28682006643406);
        building4.setOpeningTime(500);
        building4.setClosingTime(1900);
        building4.setNum_seats(12);
        building4.setSeatLocations(new boolean[]{ true, false, false, false, false, true, true, true, true, false, false, true });
        // Put building in hashmap
        buildingInfoMap.put(building4.getBuilding_id(), building4);


        BuildingInfo building5 = new BuildingInfo();
        building5.setBuilding_id("Ronald Tutor Hall");
        building5.setDescription("Ronald Tutor Hall is...");
        building5.setLatitude(34.020531023744134);
        building5.setLongitude(-118.28989707852968);
        building5.setOpeningTime(500);
        building5.setClosingTime(1100);
        building5.setNum_seats(11);
        building5.setSeatLocations(new boolean[]{ true, false, false, false, true, true, true, true, false, false, true });
        // Put building in hashmap
        buildingInfoMap.put(building5.getBuilding_id(), building5);


        BuildingInfo building6 = new BuildingInfo();
        building6.setBuilding_id("Annenberg");
        building6.setDescription("Annenberg is...");
        building6.setLatitude(34.02220456164657);
        building6.setLongitude(-118.28604156472383);
        building6.setOpeningTime(500);
        building6.setClosingTime(1900);
        building6.setNum_seats(10);
        building6.setSeatLocations(new boolean[]{ true, false, false, true, true, true, true, false, false, true });
        // Put building in hashmap
        buildingInfoMap.put(building6.getBuilding_id(), building6);


        BuildingInfo building7 = new BuildingInfo();
        building7.setBuilding_id("GFS");
        building7.setDescription("GFS is...");
        building7.setLatitude(34.02152345532107);
        building7.setLongitude(-118.28802060664995);
        building7.setOpeningTime(500);
        building7.setClosingTime(1900);
        building7.setNum_seats(9);
        building7.setSeatLocations(new boolean[]{ true, false, false, true, true, true, false, false, true });
        // Put building in hashmap
        buildingInfoMap.put(building7.getBuilding_id(), building7);


        BuildingInfo building8 = new BuildingInfo();
        building8.setBuilding_id("SGM");
        building8.setDescription("SGM is...");
        building8.setLatitude(34.02150569696153);
        building8.setLongitude(-118.28915787099945);
        building8.setOpeningTime(1000);
        building8.setClosingTime(2200);
        building8.setNum_seats(10);
        building8.setSeatLocations(new boolean[]{ true, false, false, true, true, true, false, true, false, true });
        // Put building in hashmap
        buildingInfoMap.put(building8.getBuilding_id(), building8);


        BuildingInfo building9 = new BuildingInfo();
        building9.setBuilding_id("EVK");
        building9.setDescription("EVK is...");
        building9.setLatitude(34.02156258226899);
        building9.setLongitude(-118.28220741211746);
        building9.setOpeningTime(500);
        building9.setClosingTime(1300);
        building9.setNum_seats(9);
        building9.setSeatLocations(new boolean[]{ true, false, false, true, true, true, false, false, true });
        // Put building in hashmap
        buildingInfoMap.put(building9.getBuilding_id(), building9);


        BuildingInfo building10 = new BuildingInfo();
        building10.setBuilding_id("Jefferson Boulevard Structure");
        building10.setDescription("Jefferson Boulevard Structure is...");
        building10.setLatitude(34.024959387502214);
        building10.setLongitude(-118.28945848221602);
        building10.setOpeningTime(1200);
        building10.setClosingTime(1800);
        building10.setNum_seats(7);
        building10.setSeatLocations(new boolean[]{ true, false, true, true, false, false, true });
        // Put building in hashmap
        buildingInfoMap.put(building10.getBuilding_id(), building10);


        // Insert markers on map
        for (BuildingInfo building : buildingInfoMap.values()) {
            LatLng position = new LatLng(building.getLatitude(), building.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(position).title(building.getBuilding_id()));
            marker.setTag(building.getBuilding_id());
        }


        ///// Call click handler //////
        mMap.setOnMarkerClickListener(marker -> {
            String buildingId = (String) marker.getTag();
            handleBuildingClick(buildingId);
            return false;
        });
    }

    private void handleBuildingClick(String buildingId) {
        BuildingInfo buildingInfo = buildingInfoMap.get(buildingId);
            if (buildingInfo != null && MainActivity.isLoggedIn()) {

                Intent intent = new Intent(getActivity(), BuildingPage.class);
                intent.putExtra("buildingID", buildingInfo.getBuilding_id());
                intent.putExtra("buildingDescription", buildingInfo.getDescription());
                intent.putExtra("buildingOpening", buildingInfo.getOpeningTime());
                intent.putExtra("buildingClosing", buildingInfo.getClosingTime());
                intent.putExtra("numSeats", buildingInfo.getNum_seats());
                intent.putExtra("seatLocations", buildingInfo.getSeatLocations());
                startActivity(intent);
            }
            else {
                Toast.makeText(getActivity(), "ERROR: Please log in before trying to book a reservation.", Toast.LENGTH_SHORT).show();
            }

    }
}