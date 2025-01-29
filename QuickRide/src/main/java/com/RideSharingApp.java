package main.java.com;

import main.java.com.ridesharing.models.Driver;
import main.java.com.ridesharing.models.Ride;
import main.java.com.ridesharing.models.Rider;
import main.java.com.ridesharing.search.SearchStrategy;
import main.java.com.ridesharing.search.ShortestRouteStrategy;
import main.java.com.ridesharing.services.RideService;
import main.java.com.ridesharing.services.UserService;
import main.java.com.ridesharing.utils.FuelSavingsCalculator;


import java.util.List;


public class RideSharingApp {
    public static void main(String[] args) {
        UserService userService = UserService.getInstance();
        RideService rideService = RideService.getInstance();

        Driver driver1 = new Driver(1, "Alice");
        Rider rider1 = new Rider(2, "Bob");
        userService.registerDriver(driver1);
        userService.registerRider(rider1);

        Ride ride1 = new Ride(driver1, "A", "B", 10, 20, System.currentTimeMillis(), 3);
        rideService.addRide(ride1);

        SearchStrategy strategy = new ShortestRouteStrategy();
        List<Ride> availableRides = rideService.searchRide(strategy);
        System.out.println("Available Rides: " + availableRides.size());

        rideService.bookRide(rider1, ride1);
        System.out.println("Seats left in Ride 1: " + ride1.isAvailable());

        System.out.println("Fuel saved: " + FuelSavingsCalculator.calculateSavings(2) + " liters");
    }
}
