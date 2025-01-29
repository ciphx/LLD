package main.java.com.ridesharing.models;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


public class Ride {
    private static final AtomicInteger rideCounter = new AtomicInteger(1);
    private int rideId;
    private Driver driver;
    private String source;
    private String destination;
    private int distance;
    private int estimatedTime;
    private AtomicInteger availableSeats;
    private final ReentrantLock lock = new ReentrantLock();

    public Ride(Driver driver, String source, String destination, int distance, int estimatedTime, long timestamp, int availableSeats) {
        this.rideId = rideCounter.getAndIncrement();
        this.driver = driver;
        this.source = source;
        this.destination = destination;
        this.distance = distance;
        this.estimatedTime = estimatedTime;
        this.availableSeats = new AtomicInteger(availableSeats);
    }

    public boolean isAvailable() {
        return availableSeats.get() > 0;
    }

    public boolean bookSeat() {
        lock.lock();
        try {
            if (availableSeats.get() > 0) {
                availableSeats.decrementAndGet();
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public Driver getDriver() {
        return driver;
    }

    public int getDistance() {
        return distance;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public int getRideId() {
        return rideId;
    }

    public void setRideId(int rideId) {
        this.rideId = rideId;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public AtomicInteger getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(AtomicInteger availableSeats) {
        this.availableSeats = availableSeats;
    }
}

