package com.example.findaseat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class EditReservationFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private View rootView;

    public EditReservationFragment(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.edit_reservation, container, false);
        List<Reservation> currentReservationList = ManageReservationFragment.getCurrentReservationList();
        Button cancelButton = rootView.findViewById(R.id.cancelButton);
        Reservation currReservation = currentReservationList.get(0);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Cancel")
                        .setMessage("Are you sure you want to cancel?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get current reservation here
                                // pass in current reservation and update username in function
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
        });
        return rootView;
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
