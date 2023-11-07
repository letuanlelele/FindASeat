package com.example.findaseat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findaseat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class BookingActivity extends AppCompatActivity {

    private TextView dateTextView;
    private TextView seatTextView;
    private TextView timeTextView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference users = db.collection("BuildingInfo");
    private String selectedBuilding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        dateTextView = findViewById(R.id.dateTextView);
        seatTextView = findViewById(R.id.seatTextView);
        timeTextView = findViewById(R.id.timeTextView);

        Intent intent = getIntent();
        String selectedDate = intent.getStringExtra("selectedDate");
        String selectedSeat = intent.getStringExtra("selectedSeat");
        String selectedStartTime = intent.getStringExtra("selectedStartTime");
        String selectedEndTime = intent.getStringExtra("selectedEndTime");
        selectedBuilding = intent.getStringExtra("selectedBuilding");

        dateTextView.setText("Selected Date: " + selectedDate);
        seatTextView.setText("Selected Seat: " + selectedSeat);
        timeTextView.setText("Selected Time: " + selectedStartTime);

        String[] parts = selectedSeat.split(" ");
        int seatNum = Integer.parseInt(parts[1]);
        // Call firebase function here
        firebase(selectedBuilding, selectedStartTime, selectedEndTime, seatNum);
    }

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

    private void firebase(String date, String selectedStartTime, String selectedEndTime, int selectedSeat) {
        db.collection("users")
                .document(selectedBuilding)
                .collection("reservations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String date_pull = document.getId();
                                if (date_pull == date) {
                                    // parse start time
                                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
                                    Date startTime;
                                    try {
                                        startTime = sdf.parse(selectedStartTime);
                                    } catch (ParseException e) {
                                        // Handle parsing error
                                        Log.i("myInfoTag", String.valueOf(e));
                                        return;
                                    }
                                    // parse end time
                                    int[] endTime;
                                    try {
                                        endTime = convertTo24HourFormat(selectedEndTime);
                                    } catch (ParseException e){
                                        Log.i("myInfoTag", String.valueOf(e));
                                        return;
                                    }

                                    // generate list of times to check for availability
                                    ArrayList<String> timeFieldsToCheck = new ArrayList<>();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(startTime);
                                    while (calendar.get(Calendar.HOUR_OF_DAY) != endTime[0]) {
                                        Log.i("myInfoTag", "Adding: " + SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                                        calendar.add(Calendar.MINUTE, 30);
                                        timeFieldsToCheck.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                                    }
                                    if(endTime[1] == 30){
                                        Log.i("myInfoTag", "Adding: " + SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                                        calendar.add(Calendar.MINUTE, 30);
                                        timeFieldsToCheck.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                                    }

                                    for(String timeField : timeFieldsToCheck){
                                        List<Boolean> seatAvailability = (List<Boolean>) document.get(timeField);

                                        seatAvailability.set(selectedSeat, false);

                                        DocumentReference docRef = db.collection("BuildingInfo").document(selectedBuilding).collection("reservations").document(timeField);
                                        // Update the document
                                        docRef.update(timeField, seatAvailability)
                                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully updated!"))
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("Firestore", "Error updating document", e);
                                                    }
                                                });
                                    }
                                }




                                Log.d("myInfoTag", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("myInfoTag", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}


