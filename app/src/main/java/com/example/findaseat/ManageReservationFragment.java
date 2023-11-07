package com.example.findaseat;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class ManageReservationFragment extends Fragment {
    public ManageReservationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.manage_reservation, container, false);

        Button editReservationButton = rootView.findViewById(R.id.edit_reservation_button);
        editReservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditReservationFragment editReservationsFragment = new EditReservationFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, editReservationsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        // Initialize the RecyclerView for current and past
        RecyclerView currentRecyclerView = rootView.findViewById(R.id.currentReservationRecyclerView);
        currentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        RecyclerView pastRecyclerView = rootView.findViewById(R.id.pastReservationRecyclerView);
        pastRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Reservation> currentReservationList = new ArrayList<>();
        currentReservationList.add(new Reservation("4:30 PM - 6:30 PM", "2023-11-17", "Doheny", "Seat 3", "Cancelled"));
        // Create a list of reservations (replace this with your data)
        List<Reservation> pastReservationList = new ArrayList<>();
        pastReservationList.add(new Reservation("3:30 PM - 5:00 PM", "2023-10-17", "Leavey Library", "Seat 3", "Cancelled"));
        pastReservationList.add(new Reservation("2:00 PM - 2:30 PM", "2023-10-16", "Taco Bell", "Seat 2", "Confirmed"));
        pastReservationList.add(new Reservation("9:00 AM - 10:30 AM", "2023-10-15", "Northern Cafe", "Seat 1", "Confirmed"));

        // Initialize the adapter and set it to the RecyclerView
        ReservationAdapter currentAdapter = new ReservationAdapter(currentReservationList);
        currentRecyclerView.setAdapter(currentAdapter);
        ReservationAdapter pastAdapter = new ReservationAdapter(pastReservationList);
        pastRecyclerView.setAdapter(pastAdapter);

        return rootView;
    }
}
