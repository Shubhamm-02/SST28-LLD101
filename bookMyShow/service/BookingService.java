package service;

import java.util.*;

public class BookingService {
    private Map<Integer, Set<Integer>> bookedSeats = new HashMap<>();

    public boolean bookSeats(int showId, List<Integer> seatIds) {
        bookedSeats.putIfAbsent(showId, new HashSet<>());
        Set<Integer> booked = bookedSeats.get(showId);

        for (int seat : seatIds) {
            if (booked.contains(seat)) return false;
        }

        booked.addAll(seatIds);
        return true;
    }
}
