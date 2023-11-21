package com.example.findaseat;

import static org.junit.Assert.*;

import com.google.firebase.Timestamp;

import org.junit.Assert;
import org.junit.Test;

public class ReservationTest {

    private String start_time;
    private String end_time;
    private String date;
    private String location = "Leavey";
    private String seat;
    private String status;
    private Timestamp timestamp;

    private String doc_id = "Leavey";
    private boolean cancelled = false;

    @Test
    public void getStartTime() {
    }

    @Test
    public void setStartTime() {
    }

    @Test
    public void getEndTime() {
    }

    @Test
    public void setEndTime() {
    }

    @Test
    public void getDate() {
    }

    @Test
    public void setDate() {
    }

    @Test
    public void getLocation() {
        Reservation obj = new Reservation(start_time, end_time, date, location, seat, status, timestamp, doc_id, cancelled);
//        String test_building_id = "A different building";
//        obj.setBuilding_id(test_building_id);
        Assert.assertEquals("Test getLocation", location, obj.getLocation());
    }

    @Test
    public void setLocation() {
        Reservation obj = new Reservation(start_time, end_time, date, location, seat, status, timestamp, doc_id, cancelled);
        String test = "test";
        obj.setLocation(test);
        Assert.assertEquals("Test setLocation", test, obj.getLocation());
    }

    @Test
    public void getSeat() {
    }

    @Test
    public void setSeat() {
    }

    @Test
    public void setTimestamp() {
    }

    @Test
    public void getTimestamp() {
    }

    @Test
    public void getDoc_id() {
        Reservation obj = new Reservation(start_time, end_time, date, location, seat, status, timestamp, doc_id, cancelled);
        Assert.assertEquals("Test getDoc_id", doc_id, obj.getDoc_id());
    }

    @Test
    public void setDoc_id() {
        Reservation obj = new Reservation(start_time, end_time, date, location, seat, status, timestamp, doc_id, cancelled);
        String test = "test";
        obj.setDoc_id(test);
        Assert.assertEquals("Test setDoc_id", test, obj.getDoc_id());
    }

    @Test
    public void getCancelled() {
        Reservation obj = new Reservation(start_time, end_time, date, location, seat, status, timestamp, doc_id, cancelled);
        Assert.assertEquals("Test getCancelled", cancelled, obj.getCancelled());
    }

    @Test
    public void setCancelled() {
        Reservation obj = new Reservation(start_time, end_time, date, location, seat, status, timestamp, doc_id, cancelled);
        Boolean test = true;
        obj.setCancelled(test);
        Assert.assertEquals("Test setCancelled", test, obj.getCancelled());
    }
}