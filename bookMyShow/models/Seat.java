package models;

public class Seat {
    public int id;
    public int screenId;
    public int seatNumber;

    public Seat(int id, int screenId, int seatNumber) {
        this.id = id;
        this.screenId = screenId;
        this.seatNumber = seatNumber;
    }
}
