package models;

public class Screen {
    public int id;
    public int theatreId;
    public int totalSeats;

    public Screen(int id, int theatreId, int totalSeats) {
        this.id = id;
        this.theatreId = theatreId;
        this.totalSeats = totalSeats;
    }
}
