package models;

public class Show {
    public int id;
    public int movieId;
    public int screenId;
    public String startTime;

    public Show(int id, int movieId, int screenId, String startTime) {
        this.id = id;
        this.movieId = movieId;
        this.screenId = screenId;
        this.startTime = startTime;
    }
}
