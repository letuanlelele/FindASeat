package com.example.findaseat;

import static java.lang.Integer.parseInt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookingActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String selectedBuilding;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("myInfoTag", "Check 1");

        // get page xml and text views
        setContentView(R.layout.activity_booking);
        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView seatTextView = findViewById(R.id.seatTextView);
        TextView timeTextView = findViewById(R.id.timeTextView);
        View returnToMapButton = findViewById(R.id.returnToMapButton);

        Log.i("myInfoTag", "Check 2");
        // get data from BuildingPage Activity
        Intent intent = getIntent();
        String selectedDate = intent.getStringExtra("selectedDate");
        String selectedSeat = intent.getStringExtra("selectedSeat");
        String selectedStartTime = intent.getStringExtra("selectedStartTime");
        String selectedEndTime = intent.getStringExtra("selectedEndTime");
        selectedBuilding = intent.getStringExtra("selectedBuilding");

        Log.i("myInfoTag", "Check 3");
        // User Fields: Set text views
        dateTextView.setText("Selected Date: " + selectedDate);
        seatTextView.setText("Selected Seat: " + selectedSeat);
        timeTextView.setText("Selected Time: " + selectedStartTime);

        // parse data for firebase
        // Split "Seat 2" string
        Log.i("myInfoTag", "Check 4");

//        if (!TestUtils.isRunningTest()) {
//            int seatNum = parseSelectedSeat(selectedSeat);
//
//            // Call firebase function here
//            Log.i("myInfoTag", "Check 5");
//            updateFirebaseBuilding(selectedDate, selectedStartTime, selectedEndTime, seatNum);
//            updateFirebaseUser(selectedDate, selectedStartTime, selectedEndTime, seatNum);
//        }
        int seatNum = parseSelectedSeat(selectedSeat);

        // Call firebase function here
        Log.i("myInfoTag", "Check 5");
        updateFirebaseBuilding(selectedDate, selectedStartTime, selectedEndTime, seatNum);
        updateFirebaseUser(selectedDate, selectedStartTime, selectedEndTime, seatNum);



        // Button to return to map
        returnToMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMap();
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
        assert date != null;
        calendar.setTime(date);

        // Extract the HOUR_OF_DAY and MINUTE fields
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        // Return the result in a two-element int array
        return new int[]{hourOfDay, minutes};
    }

    public static int parseSelectedSeat(String selectedSeat){
        // Define the regex pattern to match numbers
        String regex = "\\d+";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        // Matcher for the string with asterisk
        Matcher matcher = pattern.matcher(selectedSeat);

        // Find and print the number from the string without asterisk
        if (matcher.find()) {
            System.out.println(matcher.group()); // This will print "10"
        }
        Log.i("myInfoTag", matcher.group());
        return parseInt(matcher.group());
    }

    // Create new reservation in firebase
    private void updateFirebaseBuilding(String date, String selectedStartTime, String selectedEndTime, int selectedSeat) {
        Log.i("myInfoTag", "In firebase with " + selectedBuilding);
        db.collection("BuildingInfo")
                .document(selectedBuilding)
                .collection("reservations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String date_pull = document.getId();

                                if (date_pull.equals(date)) {
                                    Log.i("myInfoTag", "date_pull == date YESYES");
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

                                    // generate list of times to update availability
                                    ArrayList<String> timeFieldsToCheck = new ArrayList<>();
                                    Calendar calendar = Calendar.getInstance();
                                    assert startTime != null;
                                    calendar.setTime(startTime);
                                    while (calendar.get(Calendar.HOUR_OF_DAY) != endTime[0]) {
                                        Log.i("myInfoTag", "Adding in Booking: " + SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                                        timeFieldsToCheck.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                                        calendar.add(Calendar.MINUTE, 30);
                                    }
                                    if(endTime[1] == 30){
                                        Log.i("myInfoTag", "Adding in Booking: " + SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                                        timeFieldsToCheck.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                                        calendar.add(Calendar.MINUTE, 30);
                                    }

                                    // update each time field between start and end
                                    for(String timeField : timeFieldsToCheck){
                                        Log.d("myInfoTag", "IN FOR LOOP");
                                        // get boolean list at timeField
                                        List<Boolean> seatAvailability = (List<Boolean>) document.get(timeField);
                                        // set seat as unavailable
                                        assert seatAvailability != null;
                                        seatAvailability.set(selectedSeat-1, false);

                                        // update document in Firestore
                                        DocumentReference docRef = db.collection("BuildingInfo").document(selectedBuilding).collection("reservations").document(document.getId());
                                        docRef.update(timeField, seatAvailability)
                                                .addOnSuccessListener(aVoid -> Log.d("myInfoTag", "BRUH successfully updated to: " + timeField + " and " + seatAvailability))
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
                            Log.i("myInfoTag", "IN SUCCESSFUL");
                        } else {
                            Log.d("myInfoTag", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    // Create new reservation in users
    private void updateFirebaseUser(String date, String selectedStartTime, String selectedEndTime, int selectedSeat) {
        String username = MainActivity.getUsername();
        String TAG = "myInfoTag";

        // Create map that stores reservation info
        Map<String, Object> user_reservation_info = new HashMap<>();
        user_reservation_info.put("cancelled", false);
        user_reservation_info.put("created_timestamp", FieldValue.serverTimestamp());
        user_reservation_info.put("date", date);
        user_reservation_info.put("end_time", selectedEndTime);
        user_reservation_info.put("location", selectedBuilding);
        user_reservation_info.put("seat_num", selectedSeat);
        user_reservation_info.put("start_time", selectedStartTime);

        Log.d("myInfoTag", "user_reservation_info: " + user_reservation_info);

        db.collection("users")
                .document(username)
                .collection("user_reservations")
                .add(user_reservation_info)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void returnToMap() {
        Intent intent = new Intent(BookingActivity.this, MainActivity.class);
        startActivity(intent);
    }
}


