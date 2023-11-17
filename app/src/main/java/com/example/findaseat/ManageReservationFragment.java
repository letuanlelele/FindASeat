package com.example.findaseat;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ManageReservationFragment extends Fragment {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference users = db.collection("users");
    private String username;
    private View rootView;
    private final List<Reservation> currentReservationList = new ArrayList<>();
    private final List<Reservation> pastReservationList = new ArrayList<>();
    public ManageReservationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.manage_reservation, container, false);

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

        // Show current reservation:
        showReservations();

        // Initialize the adapter and set it to the RecyclerView
        ReservationAdapter currentAdapter = new ReservationAdapter(currentReservationList);
        currentRecyclerView.setAdapter(currentAdapter);
        ReservationAdapter pastAdapter = new ReservationAdapter(pastReservationList);
        pastRecyclerView.setAdapter(pastAdapter);

        return rootView;
    }

    private void showReservations() {
        username = ((MainActivity) requireActivity()).getUsername();

        RecyclerView currentRecyclerView = rootView.findViewById(R.id.currentReservationRecyclerView);
        currentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        RecyclerView pastRecyclerView = rootView.findViewById(R.id.pastReservationRecyclerView);
        pastRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db.collection("users")
                .document(username)
                .collection("user_reservations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                // Get data from Firebase
                                String doc_id = document.getId();

                                Boolean cancelled = document.getBoolean("cancelled");
                                String cancelled_string = "Cancelled: " + String.valueOf(cancelled);
                                Timestamp created_timestamp = document.getTimestamp("created_timestamp");
                                String date_pull = document.getString("date");
                                String date = "Date: " + date_pull;
                                String end_time = document.getString("end_time");
                                String start_time = document.getString("start_time");
                                Long seat_num_pull = document.getLong("seat_num");
                                String seat_num = "Seat number: " + seat_num_pull;
                                String location = "Location: " + document.getString("location");
                                String time = start_time + " - " + end_time;

                                // Figure out if current or past
                                if (isCurrentReservation(date_pull, end_time)) {
                                    currentReservationList.add(new Reservation(time, date, location, seat_num, cancelled_string, created_timestamp, doc_id));
                                }
                                else {
                                    pastReservationList.add(new Reservation(time, date, location, seat_num, cancelled_string, created_timestamp, doc_id));
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        // Sort past reservations: latest created first
                        pastReservationList.sort((r1, r2) -> r2.getTimestamp().compareTo(r1.getTimestamp()));

                        // Display Reservations
                        ReservationAdapter currentAdapter = new ReservationAdapter(currentReservationList);
                        currentRecyclerView.setAdapter(currentAdapter);
                        ReservationAdapter pastAdapter = new ReservationAdapter(pastReservationList);
                        pastRecyclerView.setAdapter(pastAdapter);
                    }
                });
    }

    private boolean isCurrentReservation(String dateString, String timeString) {
//        String dateString = date; // Your date string
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        try {
            Date date = dateFormat.parse(dateString);
            Date time = timeFormat.parse(timeString);
            Date currentDateTime = new Date(); // Get the current date
            Date targetDateTime = combineDateAndTime(date, time);
            Log.i("myInfoTag", "dateString: " + dateString);
            Log.i("myInfoTag", "parsed date: " + String.valueOf(date));
            Log.i("myInfoTag", "timeString " + timeString);
            Log.i("myInfoTag", "parsed time " + String.valueOf(time));
            Log.i("myInfoTag", "Target date is: " + String.valueOf(targetDateTime));
            Log.i("myInfoTag", "Current date is: " + String.valueOf(currentDateTime));


            if (targetDateTime.before(currentDateTime)) {
                System.out.println("The given date and time is in the past.");
                return false;
            }
            else {
                return true;
            }
//            else if (targetDateTime.after(currentDateTime)) {
//                System.out.println("The given date and time is in the future.");
//            } else {
//                System.out.println("The given date and time is right now.");
//            }
        } catch (ParseException e) {
            System.out.println("Invalid date format: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Date combineDateAndTime(Date date, Date time) {
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(date);

        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTime(time);

        calendarDate.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
        calendarDate.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
        calendarDate.set(Calendar.SECOND, calendarTime.get(Calendar.SECOND));
        calendarDate.set(Calendar.MILLISECOND, calendarTime.get(Calendar.MILLISECOND));

        return calendarDate.getTime();
    }




}
