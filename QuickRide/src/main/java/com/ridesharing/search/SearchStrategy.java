package main.java.com.ridesharing.search;

import main.java.com.ridesharing.models.Ride;

import java.util.List;

// SearchStrategy Interface
public interface SearchStrategy {
    List<Ride> search(List<Ride> rides);
}

