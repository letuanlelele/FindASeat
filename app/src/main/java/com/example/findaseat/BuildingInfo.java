package com.example.findaseat;

public class BuildingInfo {
    private String building_id;
    private String description;
    private double latitude;
    private double longitude;
    private int opening_time;
    private int closing_time;
    private int num_seats;
    private boolean[] locations;

    public BuildingInfo(String building_id, String description, double latitude, double longitude, int opening_time, int closing_time, int num_seats, boolean[] locations) {
        this.building_id = building_id;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.opening_time = opening_time;
        this.closing_time = closing_time;
        this.num_seats = num_seats;
        this.locations = locations;
    }

    // Empty constructor for Firestore
    public BuildingInfo() {
    }


    public String getBuilding_id() {
        return building_id;
    }

    public void setBuilding_id(String building_id) {
        this.building_id = building_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getOpeningTime() {
        return opening_time;
    }

    public void setOpeningTime(int opening_time) {
        this.opening_time = opening_time;
    }

    public int getClosingTime() {
        return closing_time;
    }

    public void setClosingTime(int closing_time) {
        this.closing_time = closing_time;
    }

    public int getNum_seats() {
        return num_seats;
    }

    public void setNum_seats(int num_seats) {
        this.num_seats = num_seats;
    }

    public void setSeatLocations(boolean[] locations) {
        this.locations = locations;
    }

    public boolean[] getSeatLocations() {
        return locations;
    }
}
