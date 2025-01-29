package main.java.com.ridesharing.search;

import main.java.com.ridesharing.models.Ride;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Shortest Route Strategy
public class ShortestRouteStrategy implements SearchStrategy {
    @Override
    public List<Ride> search(List<Ride> rides) {
        return rides.stream()
                .sorted(Comparator.comparingInt(Ride::getDistance))
                .limit(3)
                .collect(Collectors.toList());
    }
}

