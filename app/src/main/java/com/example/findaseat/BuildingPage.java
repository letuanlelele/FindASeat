package com.example.findaseat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BuildingPage extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private int numSeats;
    private int openingHour;
    private int openingMinute;
    private int closingHour;
    private int closingMinute;
    private boolean[] seatLocations;

    private Button datePickerButton;
    private Spinner startTimeSpinner;
    private Spinner endTimeSpinner;
    private GridLayout seatGrid;

    private String selectedBuilding;
    private String selectedStartTime;
    private String selectedEndTime;
    private String selectedSeat;
    private String selectedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);
        Intent intent = getIntent();

        // get number of seats
        numSeats = intent.getIntExtra("numSeats", 0);
        seatLocations = intent.getBooleanArrayExtra("seatLocations");

        // set building name text
        selectedBuilding = intent.getStringExtra("buildingID");
        TextView buildingNameTextView = findViewById(R.id.buildingNameTextView);
        buildingNameTextView.setText(selectedBuilding);

        // set building description text
        String buildingDescription = intent.getStringExtra("buildingDescription");
        TextView buildingDescriptionTextView = findViewById(R.id.buildingDescriptionText);
        buildingDescriptionTextView.setText(buildingDescription);

        /* set building opening time text */
        int buildingOpeningTime = intent.getIntExtra("buildingOpening", 900);
        // set hour value
        openingHour = (buildingOpeningTime/100);
        int openingHourAmPm = (buildingOpeningTime/100)%12;
        if(openingHourAmPm == 0){
            openingHourAmPm = 12;
        }
        // set
        openingMinute = buildingOpeningTime % 100;
        String stringOpeningMinute = Integer.toString(openingMinute);
        if(openingMinute == 0){
            stringOpeningMinute = "00";
        }
        // set am or pm value
        String openingAmPm = "am";
        if(openingHour != 24 && openingHour > 11){
            openingAmPm = "pm";
        }
        TextView buildingOpeningTextView = findViewById(R.id.buildingOpeningText);
        buildingOpeningTextView.setText("Opening Time: " + openingHourAmPm + ":" + stringOpeningMinute + openingAmPm);

        /* set building opening time text */
        int buildingClosingTime = intent.getIntExtra("buildingClosing", 2400);
        // set hour value
        closingHour = (buildingClosingTime/100);
        int closingHourAmPm = closingHour%12;
        if(closingHourAmPm == 0){
            closingHourAmPm = 12;
        }
        // set minute value
        closingMinute = buildingClosingTime % 100;
        String stringClosingMinute = Integer.toString(closingMinute);
        if(closingMinute == 0){
            stringClosingMinute = "00";
        }
        // set am or pm value
        String closingAmPm = "am";
        if(closingHour != 24 && closingHour > 11){
            closingAmPm = "pm";
        }
        TextView buildingClosingTextView = findViewById(R.id.buildingClosingText);
        buildingClosingTextView.setText("Closing Time: " + closingHourAmPm + ":" + stringClosingMinute + closingAmPm);

        datePickerButton = findViewById(R.id.datePickerButton);
        startTimeSpinner = findViewById(R.id.startTimeSpinner);
        endTimeSpinner = findViewById(R.id.endTimeSpinner);
        seatGrid = findViewById(R.id.seatGrid);

        // Add a click listener for the date picker button to show a calendar dialog
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        selectedDate = (currentMonth + 1) + "-" + currentDay + "-" + currentYear;
        datePickerButton.setText(selectedDate);
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        // Populate the start and end time spinners with intervals
        populateTimeSpinners();

        // Handle the book button click event to proceed to the booking page
        findViewById(R.id.bookButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement booking logic and transition to the booking page
                openBookingPage();
            }
        });
    }

    private void showDatePickerDialog() {
        // Get the current date using Calendar
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        selectedDate = (month + 1) + "-" + day + "-" + year; // Format the selected date
                        datePickerButton.setText(selectedDate);
                    }
                },
                // Set the initial date (e.g., the current date)
                currentYear, currentMonth, currentDay
        );
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }


    private void populateTimeSpinners() {
        // Example: Populate time intervals starting at 9:00 AM and ending at 5:00 PM with 30-minute intervals
        List<String> timeIntervals = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, openingHour);
        calendar.set(Calendar.MINUTE, openingMinute);

        // populate the start time intervals
        int closingHourPopulate = closingHour;
        if(closingHour > 23){
            closingHourPopulate = 23;
        }
        // calendar.add will go from 23->0
        while (calendar.get(Calendar.HOUR_OF_DAY) < closingHourPopulate) {
            timeIntervals.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 30);
        }
        if(closingMinute == 30){
            timeIntervals.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 30);
        }
        if(closingHour == 24){
            int count = 0;
            while(count < 2){
                timeIntervals.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                calendar.add(Calendar.MINUTE, 30);
                count++;
            }
        }

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeIntervals);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        startTimeSpinner.setAdapter(timeAdapter);
        int finalClosingHourPopulate = closingHourPopulate;
        startTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected start time
                selectedStartTime = timeAdapter.getItem(position);

                // Update the end time spinner based on the selected start time
                 populateEndTimeSpinner(finalClosingHourPopulate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                startTimeSpinner.setSelection(0);
//                endTimeSpinner.setSelection(0);
            }
        });

        // Set a default selection if needed

    }

    private void populateEndTimeSpinner(int closingHourPopulate) {
        Log.i("myInfoTag", "Spinner start: " + selectedStartTime);
        // Get the selected start time as a Date object
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
        Date startTime;
        try {
            startTime = sdf.parse(selectedStartTime);
        } catch (ParseException e) {
            // Handle parsing error
            return;
        }

        // Calculate the end time as 2 hours after the selected start time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);

        // Populate the end time spinner with intervals starting from the selected start time
        List<String> endTimeIntervals = new ArrayList<>();
        int count = 0;
        // begin endTime starting 30 minutes after
        calendar.add(Calendar.MINUTE, 30);
        endTimeIntervals.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
        while (closingHour != 24 && calendar.get(Calendar.HOUR_OF_DAY) < closingHourPopulate && count < 3) {
            calendar.add(Calendar.MINUTE, 30);
            endTimeIntervals.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
            count++;
        }
        // account for 2300 to 0000 change
        if(calendar.get(Calendar.HOUR_OF_DAY) == 23 && closingHour == 23 && closingMinute == 30){
            calendar.add(Calendar.MINUTE, 30);
            endTimeIntervals.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
        }
        if(closingHour == 24){
            while (calendar.get(Calendar.HOUR_OF_DAY) != 0 && count < 3) {
                calendar.add(Calendar.MINUTE, 30);
                endTimeIntervals.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                count++;
            }
        }

        ArrayAdapter<String> endTimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, endTimeIntervals);
        endTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endTimeSpinner.setAdapter(endTimeAdapter);

        endTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected end time
                selectedEndTime = endTimeAdapter.getItem(position);

                // generate seats
                generateSeatButtons();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedEndTime = "No end time selected";
            }
        });
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

    private void clearSeatButtons() {
        if (seatGrid != null) {
            for (int i = 0; i < seatGrid.getChildCount(); i++) {
                View childView = seatGrid.getChildAt(i);
                if (childView instanceof Button) {
                    seatGrid.removeViewAt(i);
                    i--; // Decrement i because the child count has now decreased
                }
            }
        }
    }

    private void generateSeatButtons() {
        Log.i("myInfoTag", "start: " + selectedStartTime);
        Log.i("myInfoTag", "end: " + selectedEndTime);

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

        // get selectedDate document
        DocumentReference reservationsRef = db.collection("BuildingInfo")
                .document(selectedBuilding)
                .collection("reservations")
                .document(selectedDate);

        reservationsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // selectedDate document exists
                    if (document != null && document.exists()) {
                        Log.i("myInfoTag", "in exists");
                        // Keep track of available seats
                        List<Integer> availableSeats = IntStream.range(0, numSeats).boxed().collect(Collectors.toList());

                        // Check each time field
                        for (String timeField : timeFieldsToCheck) {
                            // get boolean list of seat availability for a given date and time
                            List<Boolean> seatAvailability = (List<Boolean>) document.get(timeField);

                            if (seatAvailability != null) {
                                // check if seat is still available in next time slot
                                List<Integer> currentlyAvailableSeats = new ArrayList<>();
                                for (int seat = 0; seat < seatAvailability.size(); seat++) {
                                    if (seatAvailability.get(seat) && availableSeats.contains(seat)) {
                                        currentlyAvailableSeats.add(seat);
                                    }
                                }
                                availableSeats = currentlyAvailableSeats;
                            }
                        }

                        // Output the available seats
                        clearSeatButtons();
                        for (Integer seat : availableSeats) {
                            final Button seatButton = new Button(BuildingPage.this);
                            // check if seat is indoors or outdoors
                            boolean isIndoors = seatLocations[seat];
                            String outsideAsterisk = "";
                            if (!isIndoors) {
                                outsideAsterisk = "*";
                            }
                            int outputSeat = seat+1;
                            seatButton.setText("Seat " + outputSeat + outsideAsterisk);

                            seatButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Deselect all seat buttons
                                    for (int j = 0; j < seatGrid.getChildCount(); j++) {
                                        View childView = seatGrid.getChildAt(j);
                                        if (childView instanceof Button) {
                                            childView.setSelected(false);
                                        }
                                    }

                                    // Select the clicked seat button
                                    seatButton.setSelected(true);

                                    // Update the selected seat
                                    selectedSeat = seatButton.getText().toString();
                                }
                            });

                            seatButton.setBackgroundResource(R.drawable.seat_button_selector); // Define a selector in your resources

                            seatGrid.addView(seatButton);
                        }
                    } else {
                        Log.i("myInfoTag", "does not exit");
                        // create new time fields
                        ArrayList<String> reservationTimeFields = new ArrayList<>();
                        int closingHourPopulate = closingHour;
                        if (closingHour > 23) {
                            closingHourPopulate = 23;
                        }
                        // calendar.add will go from 23->0
                        while (calendar.get(Calendar.HOUR_OF_DAY) < closingHourPopulate) {
                            reservationTimeFields.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                            calendar.add(Calendar.MINUTE, 30);
                        }
                        if (closingMinute == 30) {
                            reservationTimeFields.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                            calendar.add(Calendar.MINUTE, 30);
                        }
                        if (closingHour == 24) {
                            int count = 0;
                            while (count < 2) {
                                reservationTimeFields.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                                calendar.add(Calendar.MINUTE, 30);
                                count++;
                            }
                        }

                        // The document does not exist, so create it with the time fields
                        Map<String, Object> newReservationFields = new HashMap<>();
                        for (String time : reservationTimeFields) {
                            // Create a boolean array of size numSeats, all set to true initially
                            Boolean[] availability = new Boolean[numSeats];
                            Arrays.fill(availability, Boolean.TRUE);
                            newReservationFields.put(time, Arrays.asList(availability));
                        }

                        // Add the new document with the generated fields
                        db.collection("Building_Info").document(selectedBuilding).collection("reservations").document(selectedDate)
                                .set(newReservationFields)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.i("myInfoTag", "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i("myInfoTag", "Error writing document", e);
                                        return;
                                    }
                                });

                        clearSeatButtons();

                        // Output the available seats
                        for (int i = 1; i <= numSeats; i++) {
                            final Button seatButton = new Button(BuildingPage.this);

                            // check if seat is indoors or outdoors
                            boolean isIndoors = seatLocations[i-1];
                            String outsideAsterisk = "";
                            if(!isIndoors){
                                outsideAsterisk = "*";
                            }
                            seatButton.setText("Seat " + i + outsideAsterisk);

                            seatButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Deselect all seat buttons
                                    for (int j = 0; j < seatGrid.getChildCount(); j++) {
                                        View childView = seatGrid.getChildAt(j);
                                        if (childView instanceof Button) {
                                            childView.setSelected(false);
                                        }
                                    }

                                    // Select the clicked seat button
                                    seatButton.setSelected(true);

                                    // Update the selected seat
                                    selectedSeat = seatButton.getText().toString();
                                }
                            });

                            seatButton.setBackgroundResource(R.drawable.seat_button_selector); // Define a selector in your resources
                            seatGrid.addView(seatButton);
                        }
                        selectedDate = null;
                    }
                } else {
                    Log.d("myInfoTag", "get failed with ", task.getException());
                }
            }
        });
    }

    private void openBookingPage() {
        // check to see if date and seat are selected or else prompt an error message
        if(selectedDate == null){
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        if(selectedSeat == null){
            Toast.makeText(this, "Please select a seat", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(BuildingPage.this, BookingActivity.class);
        Log.i("myInfoTag" , "selected date is:" + selectedDate);
        Log.i("myInfoTag" , "selected building is:" +selectedBuilding);
        Log.i("myInfoTag" , "selected start time is:" + selectedStartTime);
        Log.i("myInfoTag" , "selected end time is:" + selectedEndTime);
        Log.i("myInfoTag" , "selected seat is:" + selectedSeat);
        intent.putExtra("selectedDate", selectedDate);
        intent.putExtra("selectedBuilding", selectedBuilding);
        intent.putExtra("selectedStartTime", selectedStartTime);
        intent.putExtra("selectedEndTime", selectedEndTime);
        intent.putExtra("selectedSeat", selectedSeat);
        startActivity(intent);
    }
}
