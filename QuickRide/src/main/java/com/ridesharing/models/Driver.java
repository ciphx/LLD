package main.java.com.ridesharing.models;

import java.util.ArrayList;
import java.util.List;

// Driver class
public class Driver extends User {
    private List<Ride> ridesGiven;

    public Driver(int id, String name) {
        super(id, name);
        this.ridesGiven = new ArrayList<>();
    }

    public List<Ride> getRidesGiven() {
        return ridesGiven;
    }
}

