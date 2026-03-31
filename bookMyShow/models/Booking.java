package models;

import java.util.List;

public class Booking {
    public int id;
    public int showId;
    public List<Integer> seatIds;

    public Booking(int id, int showId, List<Integer> seatIds) {
        this.id = id;
        this.showId = showId;
        this.seatIds = seatIds;
    }
}
