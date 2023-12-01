//package com.example.findaseat;
//
//import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentTransaction;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.Timestamp;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.List;
//
//public class ManageReservationFragment extends Fragment {
//    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private final CollectionReference users = db.collection("users");
//    private String username;
//    private View rootView;
//    private final List<Reservation> currentReservationList = new ArrayList<>();
//    private final List<Reservation> pastReservationList = new ArrayList<>();
//    public ManageReservationFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        rootView = inflater.inflate(R.layout.manage_reservation, container, false);
//
//        Button editReservationButton = rootView.findViewById(R.id.edit_reservation_button);
//        editReservationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EditReservationFragment editReservationsFragment = new EditReservationFragment();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.frame_layout, editReservationsFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//            }
//        });
//        // Initialize the RecyclerView for current and past
//        RecyclerView currentRecyclerView = rootView.findViewById(R.id.currentReservationRecyclerView);
//        currentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//
//        RecyclerView pastRecyclerView = rootView.findViewById(R.id.pastReservationRecyclerView);
//        pastRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//
//        // Show current reservation:
//        showReservations();
//
//        // Initialize the adapter and set it to the RecyclerView
//        ReservationAdapter currentAdapter = new ReservationAdapter(currentReservationList);
//        currentRecyclerView.setAdapter(currentAdapter);
//        ReservationAdapter pastAdapter = new ReservationAdapter(pastReservationList);
//        pastRecyclerView.setAdapter(pastAdapter);
//
//        return rootView;
//    }
//
//    private void showReservations() {
//        username = ((MainActivity) requireActivity()).getUsername();
//
//        RecyclerView currentRecyclerView = rootView.findViewById(R.id.currentReservationRecyclerView);
//        currentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        RecyclerView pastRecyclerView = rootView.findViewById(R.id.pastReservationRecyclerView);
//        pastRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//
//        db.collection("users")
//                .document(username)
//                .collection("user_reservations")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                // Get data from Firebase
//                                String doc_id = document.getId();
//
//                                Boolean cancelled = document.getBoolean("cancelled");
//                                String cancelled_string = "Cancelled: " + String.valueOf(cancelled);
//                                Timestamp created_timestamp = document.getTimestamp("created_timestamp");
//                                String date_pull = document.getString("date");
//                                String date = "Date: " + date_pull;
//                                String end_time = document.getString("end_time");
//                                String start_time = document.getString("start_time");
//                                Long seat_num_pull = document.getLong("seat_num");
//                                String seat_num = "Seat number: " + seat_num_pull;
//                                String location = "Location: " + document.getString("location");
//                                String time = start_time + " - " + end_time;
//
//                                // Figure out if current or past
//                                if (isCurrentReservation(date_pull, end_time)) {
//                                    currentReservationList.add(new Reservation(time, date, location, seat_num, cancelled_string, created_timestamp, doc_id));
//                                }
//                                else {
//                                    pastReservationList.add(new Reservation(time, date, location, seat_num, cancelled_string, created_timestamp, doc_id));
//                                }
//
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//
//                        // Sort past reservations: latest created first
//                        pastReservationList.sort((r1, r2) -> r2.getTimestamp().compareTo(r1.getTimestamp()));
//
//                        // Display Reservations
//                        ReservationAdapter currentAdapter = new ReservationAdapter(currentReservationList);
//                        currentRecyclerView.setAdapter(currentAdapter);
//                        ReservationAdapter pastAdapter = new ReservationAdapter(pastReservationList);
//                        pastRecyclerView.setAdapter(pastAdapter);
//                    }
//                });
//    }
//
//    private boolean isCurrentReservation(String dateString, String timeString) {
////        String dateString = date; // Your date string
//        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
//        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
//
//        try {
//            Date date = dateFormat.parse(dateString);
//            Date time = timeFormat.parse(timeString);
//            Date currentDateTime = new Date(); // Get the current date
//            Date targetDateTime = combineDateAndTime(date, time);
//            Log.i("myInfoTag", "dateString: " + dateString);
//            Log.i("myInfoTag", "parsed date: " + String.valueOf(date));
//            Log.i("myInfoTag", "timeString " + timeString);
//            Log.i("myInfoTag", "parsed time " + String.valueOf(time));
//            Log.i("myInfoTag", "Target date is: " + String.valueOf(targetDateTime));
//            Log.i("myInfoTag", "Current date is: " + String.valueOf(currentDateTime));
//
//
//            if (targetDateTime.before(currentDateTime)) {
//                System.out.println("The given date and time is in the past.");
//                return false;
//            }
//            else {
//                return true;
//            }
////            else if (targetDateTime.after(currentDateTime)) {
////                System.out.println("The given date and time is in the future.");
////            } else {
////                System.out.println("The given date and time is right now.");
////            }
//        } catch (ParseException e) {
//            System.out.println("Invalid date format: " + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static Date combineDateAndTime(Date date, Date time) {
//        Calendar calendarDate = Calendar.getInstance();
//        calendarDate.setTime(date);
//
//        Calendar calendarTime = Calendar.getInstance();
//        calendarTime.setTime(time);
//
//        calendarDate.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
//        calendarDate.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
//        calendarDate.set(Calendar.SECOND, calendarTime.get(Calendar.SECOND));
//        calendarDate.set(Calendar.MILLISECOND, calendarTime.get(Calendar.MILLISECOND));
//
//        return calendarDate.getTime();
//    }
//
//
//
//
//}

package com.example.findaseat;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManageReservationFragment extends Fragment {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference users = db.collection("users");
    private String username;
    private View rootView;
    private static List<Reservation> currentReservationList = new ArrayList<>();
    private static List<Reservation> pastReservationList = new ArrayList<>();

    public static List<Reservation> getCurrentReservationList() { return currentReservationList; }

    public static void removeCurrentReservation() {
        currentReservationList.clear();
    }

    public static void updatePastReservationList(Reservation currReservation){
        currReservation.setCancelled(true);
        pastReservationList.add(currReservation);
    }
    public static List<Reservation> getPastReservationList() { return pastReservationList; }
    public ManageReservationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.manage_reservation, container, false);

        Button editReservationButton = rootView.findViewById(R.id.edit_reservation_button);
        Button cancelButton = rootView.findViewById(R.id.cancel_reservation_button);
        editReservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentReservationList.isEmpty()) {
                    Toast.makeText(getActivity(), "There is no active reservation", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Confirm Edit")
                            .setMessage("This will cancel your current reservation and prompt you to create a new one. Continue?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Reservation currReservation = currentReservationList.get(0);
                                    cancelReservationUser(currReservation);
                                    //
                                    cancelReservationBuilding(currReservation);
                                    ManageReservationFragment.removeCurrentReservation();

                                    new AlertDialog.Builder(requireContext())
                                            .setTitle("Edit Reservation Action")
                                            .setMessage("Please select the building for your reservation.")
                                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    MapFragment mapFragment = new MapFragment();
                                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                                    transaction.replace(R.id.frame_layout, mapFragment);
                                                    transaction.commit();
                                                }
                                            })
                                            .show();
                                }
                            })
                            .setNegativeButton("Return", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentReservationList.isEmpty()) {
                    Toast.makeText(getActivity(), "There is no active reservation", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Confirm Cancel")
                            .setMessage("Are you sure you want to cancel?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Get current reservation here
                                    // pass in current reservation and update username in function
                                    Reservation currReservation = currentReservationList.get(0);
                                    cancelReservationUser(currReservation);
                                    //
                                    cancelReservationBuilding(currReservation);
                                    ManageReservationFragment.removeCurrentReservation();

                                    new AlertDialog.Builder(requireContext())
                                            .setTitle("Reservation Cancelled")
                                            .setMessage("Your reservation has been cancel.")
                                            .setPositiveButton("Return", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    ProfileFragment profileFragment = new ProfileFragment();
                                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                                    transaction.replace(R.id.frame_layout, profileFragment);
                                                    transaction.commit();
                                                }
                                            })
                                            .show();
                                }
                            })
                            .setNegativeButton("Return", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            }
        });

        return rootView;
    }

    private void showReservations() {
        username = MainActivity.getUsername();

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
                            currentReservationList.clear();
                            pastReservationList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                // Get data from Firebase
                                String doc_id = document.getId();

                                Boolean cancelled = document.getBoolean("cancelled");
                                String cancelled_string = "Cancelled: " + String.valueOf(cancelled);
                                Timestamp created_timestamp = document.getTimestamp("created_timestamp");
                                String date = document.getString("date");
                                String end_time = document.getString("end_time");
                                String start_time = document.getString("start_time");
                                Long seat_num_pull = document.getLong("seat_num");
                                String seat_num = "Seat number: " + seat_num_pull;
                                String location = document.getString("location");

                                // Figure out if current or past
                                if(Boolean.TRUE.equals(cancelled)){
                                    pastReservationList.add(new Reservation(start_time, end_time, date, location, seat_num, cancelled_string, created_timestamp, doc_id, cancelled));
                                }
                                else if (isCurrentReservation(date, end_time)) {
                                    currentReservationList.add(new Reservation(start_time, end_time, date, location, seat_num, cancelled_string, created_timestamp, doc_id, cancelled));
                                }
                                else {
                                    pastReservationList.add(new Reservation(start_time, end_time, date, location, seat_num, cancelled_string, created_timestamp, doc_id, cancelled));
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

    public static boolean isCurrentReservation(String dateString, String timeString) {
        Log.i("myInfoTag", "Check reservation in Manage Reservation: " + dateString + " " + timeString);
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

    private void cancelReservationUser(Reservation reservation) {
        String username = ((MainActivity) requireActivity()).getUsername();
        String doc_id = reservation.getDoc_id();
        String TAG = "myInfoTag";
        DocumentReference docRef = db.collection("users")
                .document(username)
                .collection("user_reservations")
                .document(doc_id);

        // Set the "isCapital" field of the city 'DC'
        docRef
                .update("cancelled", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    private void cancelReservationBuilding(Reservation reservation){
        String building = reservation.getLocation();
        String date = reservation.getDate();
        String start_time = reservation.getStartTime();
        String end_time = reservation.getEndTime();
        int seat = BookingActivity.parseSelectedSeat(reservation.getSeat());

        Log.i("myInfoTag", building + " " + date + " " + start_time + " " + end_time + " " + seat);

        db.collection("BuildingInfo")
                .document(building)
                .collection("reservations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String date_pull = document.getId();
                                if (date_pull.equals(date)) {
                                    // parse start time
                                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
                                    Date startTime;
                                    try {
                                        startTime = sdf.parse(start_time);
                                    } catch (ParseException e) {
                                        // Handle parsing error
                                        Log.i("myInfoTag", String.valueOf(e));
                                        return;
                                    }
                                    // parse end time
                                    int[] endTime;
                                    try {
                                        endTime = convertTo24HourFormat(end_time);
                                    } catch (ParseException e){
                                        Log.i("myInfoTag", String.valueOf(e));
                                        return;
                                    }

                                    // generate list of times to update availability
                                    ArrayList<String> timeFieldsToCheck = new ArrayList<>();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(startTime);
                                    while (calendar.get(Calendar.HOUR_OF_DAY) != endTime[0]) {
                                        timeFieldsToCheck.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                                        calendar.add(Calendar.MINUTE, 30);
                                    }
                                    if(endTime[1] == 30){
                                        timeFieldsToCheck.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                                        calendar.add(Calendar.MINUTE, 30);
                                    }

                                    // update each time field between start and end
                                    for(String timeField : timeFieldsToCheck){
                                        // get boolean list at timeField
                                        List<Boolean> seatAvailability = (List<Boolean>) document.get(timeField);
                                        // set seat as available
                                        seatAvailability.set(seat-1, true);

                                        // update document in Firestore
                                        DocumentReference docRef = db.collection("BuildingInfo").document(building).collection("reservations").document(document.getId());
                                        docRef.update(timeField, seatAvailability)
                                                .addOnSuccessListener(aVoid -> Log.d("myInfoTag", "BRUH2 successfully updated to: " + timeField + " and " + seatAvailability))
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("myInfoTag", "Error updating document", e);
                                                    }
                                                });
                                    }
                                }
                                // log date document found
                                Log.d("myInfoTag", document.getId() + " => " + document.getData());
                            }
                            Log.i("myInfoTag", "Seat is available again");
                        } else {
                            Log.d("myInfoTag", "Error getting documents: ", task.getException());
                        }
                    }
                });



    }

    // Parses a String from to a 24-Hour Format (i.e. 5:00PM = 17)
    public static int[] convertTo24HourFormat(String timeString) throws ParseException {
        // Define the input format for parsing the time string
        SimpleDateFormat inputFormat = new SimpleDateFormat("h:mm a", Locale.US);

        // Parse the input string into a Date object
        Date date = inputFormat.parse(timeString);

        // Use Calendar to convert the Date into hours and minutes
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Extract the HOUR_OF_DAY and MINUTE fields
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        // Return the result in a two-element int array
        return new int[]{hourOfDay, minutes};
    }
}
