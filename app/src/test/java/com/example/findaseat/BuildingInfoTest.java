package com.example.findaseat;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class BuildingInfoTest {
    private String building_id = "Leavey";
    private String description = "Leavey description";
    private double latitude = 34.02204048561001;
    private double longitude = -118.28292515212222;
    private int opening_time = 830;
    private int closing_time = 2300;
    private int num_seats = 10;
    private boolean[] locations = new boolean[]{ true, true, false, false, false, true, true, true, true, false };


    @Test
    public void testGetBuilding_id() {
        BuildingInfo obj = new BuildingInfo(building_id, description, latitude, longitude, opening_time, closing_time, num_seats, locations);
        Assert.assertEquals("Test getFirstName", building_id, obj.getBuilding_id());
    }
    @Test
    public void testSetBuilding_id() {
        BuildingInfo obj = new BuildingInfo(building_id, description, latitude, longitude, opening_time, closing_time, num_seats, locations);
        String test_building_id = "A different building";
        obj.setBuilding_id(test_building_id);
        Assert.assertEquals("Test setBuilding_id", test_building_id, obj.getBuilding_id());
    }
    @Test
    public void testGetLatitude() {
        BuildingInfo obj = new BuildingInfo(building_id, description, latitude, longitude, opening_time, closing_time, num_seats, locations);
        Assert.assertEquals("Test getLatitude", latitude, obj.getLatitude(), 0);
    }
    @Test
    public void testGetLongitude() {
        BuildingInfo obj = new BuildingInfo(building_id, description, latitude, longitude, opening_time, closing_time, num_seats, locations);
        Assert.assertEquals("Test getLongitude", longitude, obj.getLongitude(), 0);
    }
    @Test
    public void testGetOpeningTime() {
        BuildingInfo obj = new BuildingInfo(building_id, description, latitude, longitude, opening_time, closing_time, num_seats, locations);
        Assert.assertEquals("Test getOpeningTime", opening_time, obj.getOpeningTime(), 0);
    }
    @Test
    public void testSetOpeningTime() {
        BuildingInfo obj = new BuildingInfo(building_id, description, latitude, longitude, opening_time, closing_time, num_seats, locations);
        int test = 1000;
        obj.setOpeningTime(test);
        Assert.assertEquals("Test setOpeningTime", test, obj.getOpeningTime(), 0);
    }
    @Test
    public void testGetClosingTime() {
        BuildingInfo obj = new BuildingInfo(building_id, description, latitude, longitude, opening_time, closing_time, num_seats, locations);
        Assert.assertEquals("Test getClosingTime", closing_time, obj.getClosingTime(), 0);
    }
    @Test
    public void testSetClosingTime() {
        BuildingInfo obj = new BuildingInfo(building_id, description, latitude, longitude, opening_time, closing_time, num_seats, locations);
        int test = 2000;
        obj.setClosingTime(test);
        Assert.assertEquals("Test setClosingTime", test, obj.getClosingTime(), 0);
    }
    @Test
    public void testGetNum_seats() {
        BuildingInfo obj = new BuildingInfo(building_id, description, latitude, longitude, opening_time, closing_time, num_seats, locations);
        Assert.assertEquals("Test getNumSeats", num_seats, obj.getNum_seats(), 0);
    }
    @Test
    public void testSetNum_seats() {
        BuildingInfo obj = new BuildingInfo(building_id, description, latitude, longitude, opening_time, closing_time, num_seats, locations);
        int test = 5;
        obj.setNum_seats(test);
        Assert.assertEquals("Test setNumSeats", test, obj.getNum_seats(), 0);
    }
    @Test
    public void testGetLocations() {
        BuildingInfo obj = new BuildingInfo(building_id, description, latitude, longitude, opening_time, closing_time, num_seats, locations);
        Assert.assertArrayEquals("Test getLocations", locations, obj.getSeatLocations());
    }
    @Test
    public void testSetLocations() {
        BuildingInfo obj = new BuildingInfo(building_id, description, latitude, longitude, opening_time, closing_time, num_seats, locations);
        boolean[] test = new boolean[]{ true, true };
        obj.setSeatLocations(test);
        Assert.assertArrayEquals("Test setLocations", test, obj.getSeatLocations());
    }
}