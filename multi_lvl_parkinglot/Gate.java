package multi_lvl_parkinglot;

public class Gate {
    private String id;
    private int floor;
    private double x, y;

    public Gate(String id, int floor, double x, double y) {
        this.id = id;
        this.floor = floor;
        this.x = x;
        this.y = y;
    }

    public String getId() { return id; }
    public int getFloor() { return floor; }
    public double getX() { return x; }
    public double getY() { return y; }
}