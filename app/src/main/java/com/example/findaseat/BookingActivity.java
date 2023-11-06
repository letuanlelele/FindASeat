package com.example.findaseat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.findaseat.R;

public class BookingActivity extends AppCompatActivity {

    private TextView dateTextView;
    private TextView seatTextView;
    private TextView timeTextView;

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

        dateTextView.setText("Selected Date: " + selectedDate);
        seatTextView.setText("Selected Seat: " + selectedSeat);
        timeTextView.setText("Selected Time: " + selectedStartTime);
    }
}


