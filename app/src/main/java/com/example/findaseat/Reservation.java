package com.example.findaseat;

public class Reservation {
    private String time;
    private String date;
    private String location;
    private String seat;
    private String status;

    public Reservation(String time, String date, String location, String seat, String status) {
        this.time = time;
        this.date = date;
        this.location = location;
        this.seat = seat;
        this.status = status;
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
}

