package main.java.com.ridesharing.services;

import main.java.com.ridesharing.models.Driver;
import main.java.com.ridesharing.models.Ride;
import main.java.com.ridesharing.models.Rider;
import main.java.com.ridesharing.search.SearchStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RideService {
    private static class Holder {
        private static final RideService INSTANCE = new RideService();
    }
    public static RideService getInstance() {
        return Holder.INSTANCE;
    }

    private List<Ride> rides;

    private RideService() {
        this.rides = Collections.synchronizedList(new ArrayList<>());
    }

    public void addRide(Ride ride) {
        synchronized (rides) {
            rides.add(ride);
        }
    }

    public boolean bookRide(Rider rider, Ride ride) {
        if (ride.bookSeat()) {
            synchronized (rider) {
                rider.getRidesTaken().add(ride);
            }
            return true;
        }
        return false;
    }

    public List<Ride> searchRide(SearchStrategy strategy) {
        return strategy.search(new ArrayList<>(rides));
    }

    public List<Ride> getRidesByDriver(Driver driver) {
        return rides.stream().filter(ride -> ride.getDriver().equals(driver)).collect(Collectors.toList());
    }

    public List<Ride> getRidesByRider(Rider rider) {
        return rider.getRidesTaken();
    }
}

