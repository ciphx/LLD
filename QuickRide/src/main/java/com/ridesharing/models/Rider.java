package main.java.com.ridesharing.models;


import java.util.ArrayList;
import java.util.List;

// Rider class
public class Rider extends User {
    private List<Ride> ridesTaken;

    public Rider(int id, String name) {
        super(id, name);
        this.ridesTaken = new ArrayList<>();
    }

    public List<Ride> getRidesTaken() {
        return ridesTaken;
    }
}

