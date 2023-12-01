package com.example.findaseat;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static com.example.findaseat.ManageReservationFragment.combineDateAndTime;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private FirebaseFirestore db;
    private int numSeats;
    private int openingHour;
    private String stringOpeningMinute;
    private String openingAmPm;
    private int openingHour12H;
    private int openingMinute;
    private int closingHour;
    private String closingAmPm;
    private int closingHour12H;
    private int closingMinute;
    private String stringClosingMinute;
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

    List<Integer> availableSeats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);
        Intent intent = getIntent();

        ///////// DISPlAY BUILDING OPENING/CLOSING TIME AFTER CLICKING ON MAP MARKER
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
        parseHHMMa(buildingOpeningTime, true);

        TextView buildingOpeningTextView = findViewById(R.id.buildingOpeningText);
        buildingOpeningTextView.setText("Opening Time: " + openingHour12H + ":" + stringOpeningMinute + openingAmPm);

        /* set building closing time text */
        int buildingClosingTime = intent.getIntExtra("buildingClosing", 2400);
        parseHHMMa(buildingClosingTime, false);

        TextView buildingClosingTextView = findViewById(R.id.buildingClosingText);
        buildingClosingTextView.setText("Closing Time: " + closingHour12H + ":" + stringClosingMinute + closingAmPm);

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
                List<Reservation> currReservation = ManageReservationFragment.getCurrentReservationList();
                if (currReservation.isEmpty()) {
                    openBookingPage();
                } else {
                    Toast.makeText(getApplicationContext(), "Reservation already exists.", Toast.LENGTH_LONG).show();
                }
                // Check if user already has current reservation
                // If they do, display toast
                // If they don't, book the reservation
                //checkIfUserAlreadyHasReservation();
            }
        });
    }

    public void checkIfUserAlreadyHasReservation() {
        String username = MainActivity.getUsername();
        List<Integer> tempList = new ArrayList<>();
        db.collection("users")
                .document(username)
                .collection("user_reservations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String doc_id = document.getId();

                                Boolean cancelled = document.getBoolean("cancelled");
                                String date = document.getString("date");
                                String end_time = document.getString("end_time");

                                // Figure out if current or past
                                Log.i("myInfoTag", "Check reservation for: " + date + " " + end_time);
                                if (isCurrentReservation(date, end_time) && cancelled != true) {
                                    tempList.add(1);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        if (tempList.isEmpty()) {
                            openBookingPage();
                        } else {
                            Toast.makeText(getApplicationContext(), "ERROR: You already have a reservation.", Toast.LENGTH_LONG).show();
                        }
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
            } else {
                return true;
            }
        } catch (ParseException e) {
            System.out.println("Invalid date format: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void parseHHMMa(int time, boolean isOpeningTime) {
        if (isOpeningTime) {
            // set hour value
            openingHour = (time / 100);
            openingHour12H = (time / 100) % 12;
            if (openingHour12H == 0) {
                openingHour12H = 12;
            }

            // set
            openingMinute = time % 100;
            stringOpeningMinute = Integer.toString(openingMinute);

            if (openingMinute == 0) {
                stringOpeningMinute = "00";
            }
            // set am or pm value
            openingAmPm = "am";
            if (openingHour != 24 && openingHour > 11) {
                openingAmPm = "pm";
            }
        } else {
            // set hour value
            closingHour = (time / 100);
            closingHour12H = closingHour % 12;
            if (closingHour12H == 0) {
                closingHour12H = 12;
            }
            // set minute value
            closingMinute = time % 100;
            stringClosingMinute = Integer.toString(closingMinute);
            if (closingMinute == 0) {
                stringClosingMinute = "00";
            }
            // set am or pm value
            closingAmPm = "am";
            if (closingHour != 24 && closingHour > 11) {
                closingAmPm = "pm";
            }
        }
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

                        populateTimeSpinners();
                    }
                },
                // Set the initial date (e.g., the current date)
                currentYear, currentMonth, currentDay
        );
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }


    private void restrictTimeSpinners(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma");
        try {
            Date date = dateFormat.parse(selectedDate);
            Date openingTime = timeFormat.parse(openingHour12H + ":" + stringOpeningMinute + openingAmPm);
            Date closingTime = timeFormat.parse(closingHour12H + ":" + stringClosingMinute + closingAmPm);
            Date currentDateTime = new Date(); // Get the current date

            Date openingDateTime = combineDateAndTime(date, openingTime);
            Date closingDateTime = combineDateAndTime(date, closingTime);


            // if current > closing
            // toast "can't book"
            if (currentDateTime.compareTo(closingDateTime) > 0) {
                Toast.makeText(getApplicationContext(), "This building is closed for today. Please select a different date.", Toast.LENGTH_LONG).show();
            }

            int result = currentDateTime.compareTo(openingDateTime);
            // currentDateTime < openingDateTime
            if (result < 0) {
                calendar.set(Calendar.HOUR_OF_DAY, openingHour);
                calendar.set(Calendar.MINUTE, openingMinute);

            }
            // openingDateTime < currentDateTime
            else {
                // round up to next hour
                if (calendar.get(Calendar.MINUTE) > 29) {
                    calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
                    calendar.set(Calendar.MINUTE, 0);
                }
                // round up to next 30 minutes
                else {
                    calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, 30);
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void populateTimeSpinners() {
        // Example: Populate time intervals starting at 9:00 AM and ending at 5:00 PM with 30-minute intervals
        List<String> timeIntervals = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        restrictTimeSpinners(calendar);


        // populate the start time intervals
        int closingHourPopulate = closingHour;
        if (closingHour > 23) {
            closingHourPopulate = 23;
        }
        // calendar.add will go from 23->0
        while (calendar.get(Calendar.HOUR_OF_DAY) < closingHourPopulate) {
            timeIntervals.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 30);
        }
        if (closingMinute == 30) {
            timeIntervals.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 30);
        }
        if (closingHour == 24) {
            int count = 0;
            while (count < 2) {
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
        if (calendar.get(Calendar.HOUR_OF_DAY) == 23 && closingHour == 23 && closingMinute == 30) {
            calendar.add(Calendar.MINUTE, 30);
            endTimeIntervals.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
        }
        if (closingHour == 24) {
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

    public int[] convertTo24HourFormat(String timeString) throws ParseException {
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

    /* SEAT BUTTON FUNCTIONS */
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

    private void updateSeatButtons(List<Integer> availableSeats, Boolean docExists) {
        if (docExists) {
            for (Integer seat : availableSeats) {
                // create new buttons
                final Button seatButton = new Button(BuildingPage.this);

                // check if seat is indoors or outdoors and mark as indoor/outdoors
                boolean isIndoors = seatLocations[seat];
                String outsideAsterisk = "";
                if (!isIndoors) {
                    outsideAsterisk = "*";
                }
                int outputSeat = seat + 1;  // convert Integer to int
                seatButton.setText("Seat " + outputSeat + outsideAsterisk);

                updateSeatButtonsHelper(seatButton);
            }
        } else {
            for (int i = 1; i <= numSeats; i++) {
                // create new buttons
                final Button seatButton = new Button(BuildingPage.this);

                // check if seat is indoors or outdoors
                boolean isIndoors = seatLocations[i - 1];
                String outsideAsterisk = "";
                if (!isIndoors) {
                    outsideAsterisk = "*";
                }
                seatButton.setText("Seat " + i + outsideAsterisk);

                updateSeatButtonsHelper(seatButton);
            }
        }
    }

    private void updateSeatButtonsHelper(Button seatButton) {
        seatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Deselect all seat buttons when new seat is deselected
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
        // Display seat button
        seatButton.setBackgroundResource(R.drawable.seat_button_selector);
        seatGrid.addView(seatButton);
    }

    private void createNewDateDocument(Map<String, Object> newReservationFields) {
        db.collection("BuildingInfo").document(selectedBuilding).collection("reservations").document(selectedDate)
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
    }


    private void generateSeatButtons() {
        Log.i("myInfoTag", "Start Time before sdf parse: " + selectedStartTime);
        Log.i("myInfoTag", "end: " + selectedEndTime);

        // parse start time
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date startTime;
        try {
            startTime = sdf.parse(selectedStartTime);
            Log.i("myInfoTag", "Start Time after sdf parse: " + startTime);
        } catch (ParseException e) {
            // Handle parsing error
            Log.i("myInfoTag", String.valueOf(e));
            return;
        }
        // parse end time from string to array: {0: hours, 1: minutes}
        int[] endTime;
        try {
            endTime = convertTo24HourFormat(selectedEndTime);
        } catch (ParseException e) {
            Log.i("myInfoTag", String.valueOf(e));
            return;
        }

        // create calendar instance to generate time fields
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);

        // get selectedDate document
        Log.d("myInfoTag", selectedBuilding + " " + selectedDate);
        DocumentReference reservationsRef = db.collection("BuildingInfo")
                .document(selectedBuilding)
                .collection("reservations")
                .document(selectedDate);

        reservationsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    /* DOCUMENT EXISTS */
                    /* Date selected found in current database */
                    if (document != null && document.exists()) {
                        Log.i("myInfoTag", "in exists");
                        // List to track of available seats
                        availableSeats = IntStream.range(0, numSeats).boxed().collect(Collectors.toList());

                        // generate ArrayList of times to check from startTime to endTime
                        ArrayList<String> timeFieldsToCheck = new ArrayList<>();
                        while (calendar.get(Calendar.HOUR_OF_DAY) != endTime[0]) {
                            Log.i("myInfoTag", "Add to timeFieldsToCheck: " + SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                            timeFieldsToCheck.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                            calendar.add(Calendar.MINUTE, 30);
                        }
                        if (endTime[1] == 30) {
                            Log.i("myInfoTag", "Add to timeFieldsToCheck: " + SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                            timeFieldsToCheck.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                            calendar.add(Calendar.MINUTE, 30);
                        }

                        Log.i("myInfoTag", "Time Fields: " + timeFieldsToCheck);
                        // Check each time field from arraylist of generated time fields
                        for (String timeField : timeFieldsToCheck) {
                            // get boolean list of seat availability for a given date and time
                            List<Boolean> seatAvailability = (List<Boolean>) document.get(timeField);

                            if (seatAvailability != null) {
                                // check if each seat is currently available and was also previously available
                                List<Integer> currentlyAvailableSeats = new ArrayList<>();
                                for (int seat = 0; seat < seatAvailability.size(); seat++) {
                                    Log.i("myInfoTag", seat + ": " + seatAvailability.get(seat) + " " + availableSeats.contains(seat));
                                    if (seatAvailability.get(seat) && availableSeats.contains(seat)) {
                                        currentlyAvailableSeats.add(seat);
                                    }
                                }
                                // update list of available seats
                                availableSeats = currentlyAvailableSeats;

                            }
                        }

                        // Output the available seats
                        clearSeatButtons();
                        // If no seats are available, output a toast saying so
                        if (availableSeats.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "No seats are availble at this time, please select a different time.", Toast.LENGTH_LONG).show();
                        } else {
                            updateSeatButtons(availableSeats, true);
                        }

                    }

                    /* DOCUMENT DOESN'T EXIST */
                    /* Date selected not found in current database */
                    else {
                        Log.i("myInfoTag", "Document does not exist");
                        // Populate array of available booking times from start to end time
                        ArrayList<String> reservationTimeFields = populateReservationTimeFields(startTime);


                        // create boolean arrays of size seatNums within new Date Document
                        Map<String, Object> newReservationFields = new HashMap<>();
                        for (String time : reservationTimeFields) {
                            Boolean[] availability = new Boolean[numSeats];
                            // set all seats to available
                            Arrays.fill(availability, Boolean.TRUE);
                            newReservationFields.put(time, Arrays.asList(availability));
                        }


                        // Add the new date document with the generated fields to Firestore
                        createNewDateDocument(newReservationFields);
                        // Output the available seats
                        clearSeatButtons();
                        updateSeatButtons(availableSeats, false);
                    }
                } else {
                    Log.d("myInfoTag", "get failed with ", task.getException());
                }
            }
        });
    }

    private ArrayList<String> populateReservationTimeFields(Date startTime) {
        // create calendar instance to generate time fields
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);

        ArrayList<String> reservationTimeFields = new ArrayList<>();


        int closingHourPopulate = closingHour;
        if (closingHour > 23) {
            closingHourPopulate = 23;
        }
        Log.i("myInfoTag", "The closing hour is updated from " + closingHour + " to " + closingHourPopulate);
        // create new time intervals up to closing time or 23
        while (calendar.get(Calendar.HOUR_OF_DAY) < closingHourPopulate) {
            Log.i("myInfoTag", "Add to reservationTimeFields: " + SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
            reservationTimeFields.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 30);
        }
        // add additional time slot to account for 30 minutes
        if (closingMinute == 30) {
            Log.i("myInfoTag", "Add to reservationTimeFields: " + SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
            reservationTimeFields.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 30);
        }
        // add 2 aditional time slots to account for 24
        if (closingHour == 24) {
            int count = 0;
            while (count < 2) {
                Log.i("myInfoTag", "Add to reservationTimeFields: " + SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                reservationTimeFields.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
                calendar.add(Calendar.MINUTE, 30);
                count++;
            }
        }
        return reservationTimeFields;
    }


    private void openBookingPage() {
        // error handling for empty seat and date
        if (selectedDate == null) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedSeat == null) {
            Toast.makeText(this, "Please select a seat", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(BuildingPage.this, BookingActivity.class);
        Log.i("myInfoTag", "selected date is:" + selectedDate);
        Log.i("myInfoTag", "selected building is:" + selectedBuilding);
        Log.i("myInfoTag", "selected start time is:" + selectedStartTime);
        Log.i("myInfoTag", "selected end time is:" + selectedEndTime);
        Log.i("myInfoTag", "selected seat is:" + selectedSeat);

        // send data to BookingActivity
        intent.putExtra("selectedDate", selectedDate);
        intent.putExtra("selectedBuilding", selectedBuilding);
        intent.putExtra("selectedStartTime", selectedStartTime);
        intent.putExtra("selectedEndTime", selectedEndTime);
        intent.putExtra("selectedSeat", selectedSeat);

        // start Booking Activity
        startActivity(intent);
    }
}
