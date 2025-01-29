package main.java.com.ridesharing.services;

import main.java.com.ridesharing.models.Driver;
import main.java.com.ridesharing.models.Rider;

import java.util.ArrayList;
import java.util.List;

// UserManager (Singleton without synchronization overhead)
public class UserService {
    private static class Holder {
        private static final UserService INSTANCE = new UserService();
    }
    public static UserService getInstance() {
        return Holder.INSTANCE;
    }

    private List<Rider> riders;
    private List<Driver> drivers;

    private UserService() {
        this.riders = new ArrayList<>();
        this.drivers = new ArrayList<>();
    }

    public void registerRider(Rider rider) {
        riders.add(rider);
    }

    public void registerDriver(Driver driver) {
        drivers.add(driver);
    }
}

