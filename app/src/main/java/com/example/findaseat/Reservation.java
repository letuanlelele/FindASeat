package com.example.findaseat;

import com.google.firebase.Timestamp;

public class Reservation {
    private String time;
    private String date;
    private String location;
    private String seat;
    private String status;
    private Timestamp timestamp;

    private String doc_id;

    public Reservation(String time, String date, String location, String seat, String status, Timestamp timestamp, String doc_id) {
        this.time = time;
        this.date = date;
        this.location = location;
        this.seat = seat;
        this.status = status;
        this.timestamp = timestamp;
        this.doc_id = doc_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
}

