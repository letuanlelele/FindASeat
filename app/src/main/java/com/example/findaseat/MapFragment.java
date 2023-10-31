package com.example.findaseat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

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
//        fetchBuildingData();
    }

//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap) {
//        mMap = googleMap;
//    }

    // Used for importing Google Map
    // Set bounds to confine the map to the campus
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Set the bounds to confine the map to the campus
        LatLngBounds campusBounds = new LatLngBounds(
                new LatLng(34.019043067462896, -118.29109258963628),       // Bottom left corner

                new LatLng(34.025442742634105, -118.28045999644873)        // Top right corner
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(campusBounds, 100));
        // Add markers for buildings and set click listeners
        fetchBuildingData();
        addBuildingMarkers();
        LatLng Fertitta = new LatLng(34.01887742105534, -118.28238833775792);
        mMap.addMarker(new MarkerOptions().position(Fertitta).title("Marker"));
    }




    private Map<String, BuildingInfo> buildingInfoMap = new HashMap<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void fetchBuildingData() {
        db.collection("BuildingInfo").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    BuildingInfo building = document.toObject(BuildingInfo.class);
                    building.setId(document.getId()); // Set the Firestore document ID
                    buildingInfoMap.put(building.getId(), building);
                }
                // Now you can add markers to the map using this data
//                addBuildingMarkers();
            } else {
                // Handle the error
                Log.d("MapFragment", "Error getting documents: ", task.getException());
            }
        });
    }

    private void addBuildingMarkers() {
        for (BuildingInfo building : buildingInfoMap.values()) {
            LatLng position = new LatLng(building.getLatitude(), building.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions().position(position).title(building.getName()));
            marker.setTag(building.getId());
        }

        mMap.setOnMarkerClickListener(marker -> {
            String buildingId = (String) marker.getTag();
            handleBuildingClick(buildingId);
            return false;
        });
    }

    private void handleBuildingClick(String buildingId) {
        BuildingInfo buildingInfo = buildingInfoMap.get(buildingId);
        if (buildingInfo != null) {
            // Create a new BottomSheetDialog
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
            View view = LayoutInflater.from(getContext()).inflate(R.layout.building_info_layout, null);
            bottomSheetDialog.setContentView(view);

            // Set the building's information to the views
            TextView nameTextView = view.findViewById(R.id.building_name);
            TextView descriptionTextView = view.findViewById(R.id.building_description);
            nameTextView.setText(buildingInfo.getName());
            descriptionTextView.setText(buildingInfo.getDescription());

            // Show the BottomSheetDialog
            bottomSheetDialog.show();
        }
    }


    private void displayBuildingInfo(String buildingName, String description) {
        // Create a new BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.building_info_layout, null);
        bottomSheetDialog.setContentView(view);

        // Set the building's information to the views
        TextView nameTextView = view.findViewById(R.id.building_name);
        TextView descriptionTextView = view.findViewById(R.id.building_description);
        nameTextView.setText(buildingName);
        descriptionTextView.setText(description);

        // Show the BottomSheetDialog
        bottomSheetDialog.show();
    }



    public class BuildingInfo {
        private String id; // Firestore document ID
        private String name;
        private String description;
        private double latitude;
        private double longitude;

        // Empty constructor for Firestore
        public BuildingInfo() {}

        // Getters and setters for all fields
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }

}