package com.example.findaseat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {
    private final List<Reservation> reservationList;

    public ReservationAdapter(List<Reservation> reservations) {
        this.reservationList = reservations;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_item, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservationList.get(position);
        holder.bind(reservation);
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {
        private final TextView startTimeTextView;

        private final TextView endTimeTextView;

        private final TextView dateTextView;
        private final TextView locationTextView;
        private final TextView seatTextView;
        private final TextView statusTextView;

        public ReservationViewHolder(View itemView) {
            super(itemView);
            startTimeTextView = itemView.findViewById(R.id.startTimeTextView);
            endTimeTextView = itemView.findViewById(R.id.endTimeTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            seatTextView = itemView.findViewById(R.id.seatTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }

        public void bind(Reservation reservation) {
            String start_time = "Start time: " + reservation.getStartTime();
            startTimeTextView.setText(start_time);
            String end_time = "End time: " + reservation.getEndTime();
            endTimeTextView.setText(end_time);
            String date = "Date: " + reservation.getDate();
            dateTextView.setText(date);
            String location = "Location: " + reservation.getLocation();
            locationTextView.setText(location);
            seatTextView.setText(reservation.getSeat());
            statusTextView.setText(reservation.getStatus());
        }
    }
}

