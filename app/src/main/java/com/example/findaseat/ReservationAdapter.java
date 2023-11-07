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
        private final TextView timeTextView;
        private final TextView dateTextView;
        private final TextView locationTextView;
        private final TextView seatTextView;
        private final TextView statusTextView;

        public ReservationViewHolder(View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            seatTextView = itemView.findViewById(R.id.seatTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }

        public void bind(Reservation reservation) {
            timeTextView.setText(reservation.getTime());
            dateTextView.setText(reservation.getDate());
            locationTextView.setText(reservation.getLocation());
            seatTextView.setText(reservation.getSeat());
            statusTextView.setText(reservation.getStatus());
        }
    }
}

