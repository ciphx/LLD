package main.java.com.ridesharing.search;

import main.java.com.ridesharing.models.Ride;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Fastest Route Strategy
class FastestRouteStrategy implements SearchStrategy {
    @Override
    public List<Ride> search(List<Ride> rides) {
        return rides.stream()
                .sorted(Comparator.comparingInt(Ride::getEstimatedTime))
                .limit(3)
                .collect(Collectors.toList());
    }
}

