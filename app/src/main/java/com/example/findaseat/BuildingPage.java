package com.example.a310_test;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BuildingPage extends AppCompatActivity {

    private TextView buildingNameTextView;
    private Button datePickerButton;
    private HorizontalScrollView seatScrollView;
    private LinearLayout seatContainer;
    private Button bookButton;

    private String selectedDate;
    private List<Button> seatButtons = new ArrayList<>();
    private List<Button> timeButtons = new ArrayList<>();
    private int maxConsecutiveSelection = 4;

    private Spinner startTimeSpinner;
    private Spinner endTimeSpinner;
    private GridLayout seatGrid;

    private String selectedBuilding;
    private String selectedStartTime;
    private String selectedEndTime;
    private String selectedSeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        selectedBuilding = "Your Building";
        datePickerButton = findViewById(R.id.datePickerButton);
        startTimeSpinner = findViewById(R.id.startTimeSpinner);
        endTimeSpinner = findViewById(R.id.endTimeSpinner);
        seatGrid = findViewById(R.id.seatGrid);
        buildingNameTextView = findViewById(R.id.buildingNameTextView);
        bookButton = findViewById(R.id.bookButton);

        // Add a click listener for the date picker button to show a calendar dialog
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement date picker dialog
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
                        selectedDate = year + "-" + (month + 1) + "-" + day; // Format the selected date
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
        calendar.add(Calendar.HOUR, 2);

        // Populate the end time spinner with intervals starting from the selected start time
        List<String> endTimeIntervals = new ArrayList<>();
        endTimeIntervals.add(selectedStartTime); // Add the selected start time
        while (calendar.getTime().before(sdf.getCalendar().getTime())) {
            endTimeIntervals.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 30);
        }

        ArrayAdapter<String> endTimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, endTimeIntervals);
        endTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endTimeSpinner.setAdapter(endTimeAdapter);
    }

//    private void openBookingPage() {
//        Intent intent = new Intent(MainActivity.this, BookingActivity.class);
//        intent.putExtra("selectedDate", selectedDate);
//
//        // Assuming you have variables to store the selected seat and time
//        // Replace "selectedSeat" and "selectedTime" with your actual variables
//        Button seatButton = findViewById(R.id.seatButton);
//        String seatValue = seatButton.getText().toString();
//        intent.putExtra("selectedSeat", seatValue);
//        Button timeButton = findViewById(R.id.timeButton);
//        String timeValue = timeButton.getText().toString();
//        intent.putExtra("selectedTime", timeValue);
//
//        startActivity(intent);
//    }

    private void openBookingPage() {
        Intent intent = new Intent(BuildingPage.this, BookingActivity.class);
        intent.putExtra("selectedBuilding", selectedBuilding);
        intent.putExtra("selectedStartTime", selectedStartTime);
        intent.putExtra("selectedEndTime", selectedEndTime);
        intent.putExtra("selectedSeat", selectedSeat);
        startActivity(intent);
    }
}
