package com.example.findaseat;

import com.google.firebase.Timestamp;

public class Reservation {
    private String start_time;
    private String end_time;
    private String date;
    private String location;
    private String seat;
    private String status;
    private Timestamp timestamp;

    private String doc_id;
    private boolean cancelled;

    public Reservation(String start_time, String end_time, String date, String location, String seat, String status, Timestamp timestamp, String doc_id, boolean cancelled) {
        this.start_time = start_time;
        this.end_time = end_time;
        this.date = date;
        this.location = location;
        this.seat = seat;
        this.status = status;
        this.timestamp = timestamp;
        this.doc_id = doc_id;
        this.cancelled = cancelled;
    }

    public String getStartTime() {
        return start_time;
    }

    public void setStartTime(String start_time) {
        this.start_time = start_time;
    }

    public String getEndTime() {
        return end_time;
    }

    public void setEndTime() {
        this.end_time = end_time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }


    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public boolean getCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

