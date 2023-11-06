package com.example.findaseat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BuildingPage extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        // set building name text
        selectedBuilding = intent.getStringExtra("buildingID");
        TextView buildingNameTextView = findViewById(R.id.buildingNameTextView);
        buildingNameTextView.setText(selectedBuilding);

        // set building description text
        String buildingDescription = intent.getStringExtra("buildingDescription");
        TextView buildingDescriptionTextView = findViewById(R.id.buildingDescriptionText);
        buildingDescriptionTextView.setText(buildingDescription);
        Log.d("myInfoTag", "Building description is: " + buildingDescription);

        // set building opening time text
        int buildingOpeningTime = intent.getIntExtra("buildingOpening", 900);
        TextView buildingOpeningTextView = findViewById(R.id.buildingOpeningText);
        buildingOpeningTextView.setText("Closing Time: " + buildingOpeningTime);

        // set building opening time text
        int buildingClosingTime = intent.getIntExtra("buildingClosing", 2400);
        TextView buildingClosingTextView = findViewById(R.id.buildingClosingText);
        buildingClosingTextView.setText("Opening Time: " + buildingClosingTime);

        datePickerButton = findViewById(R.id.datePickerButton);
        startTimeSpinner = findViewById(R.id.startTimeSpinner);
        endTimeSpinner = findViewById(R.id.endTimeSpinner);
        seatGrid = findViewById(R.id.seatGrid);

        // Add a click listener for the date picker button to show a calendar dialog
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        // Populate the start and end time spinners with intervals
        populateTimeSpinners();
        // Generate and display seat buttons
        generateSeatButtons();

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
        datePickerDialog.show();
    }

    private void generateSeatButtons() {
        for (int i = 1; i <= 10; i++) {
            final Button seatButton = new Button(this);
            seatButton.setText("Seat " + i);

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
    }


    private void populateTimeSpinners() {
        // Example: Populate time intervals starting at 9:00 AM and ending at 5:00 PM with 30-minute intervals
        List<String> timeIntervals = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);

        while (calendar.get(Calendar.HOUR_OF_DAY) < 17) {
            timeIntervals.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 30);
        }

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeIntervals);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        startTimeSpinner.setAdapter(timeAdapter);
        startTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected start time
                selectedStartTime = timeAdapter.getItem(position);

                // Update the end time spinner based on the selected start time
                populateEndTimeSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                startTimeSpinner.setSelection(0);
                endTimeSpinner.setSelection(0);
            }
        });

        // Set a default selection if needed

    }

    private void populateEndTimeSpinner() {
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
        while (count < 4) {
            endTimeIntervals.add(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 30);
            count++;
        }

        ArrayAdapter<String> endTimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, endTimeIntervals);
        endTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endTimeSpinner.setAdapter(endTimeAdapter);

        endTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected end time
                selectedEndTime = endTimeAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedEndTime = "No end time selected";
            }
        });
    }

    private void openBookingPage() {
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
