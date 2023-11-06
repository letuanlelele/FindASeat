package com.example.a310_test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BookingActivity extends AppCompatActivity {

    private TextView dateTextView;
    private TextView seatTextView;
    private TextView timeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        Intent intent = getIntent();
        String selectedDate = intent.getStringExtra("selectedDate");
        String selectedSeat = intent.getStringExtra("selectedSeat");
        String selectedTime = intent.getStringExtra("selectedTime");

        dateTextView.setText("Selected Date: " + selectedDate);
        seatTextView.setText("Selected Seat: " + selectedSeat);
        timeTextView.setText("Selected Time: " + selectedTime);
    }
}


