package multi_lvl_parkinglot;
public class Slot {
    private String id;
    private SlotType type;
    private boolean isOccupied;
    private int floor;
    private double x, y;

    public Slot(String id, SlotType type, int floor, double x, double y) {
        this.id = id;
        this.type = type;
        this.floor = floor;
        this.x = x;
        this.y = y;
        this.isOccupied = false;
    }

    public String getId() { return id; }
    public SlotType getType() { return type; }
    public boolean isOccupied() { return isOccupied; }
    public void occupy() { this.isOccupied = true; }
    public void free() { this.isOccupied = false; }
    public int getFloor() { return floor; }

    public double calculateDistanceTo(Gate gate) {
        double dx = this.x - gate.getX();
        double dy = this.y - gate.getY();
        double dz = (this.floor - gate.getFloor()) * 10.0; 
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
}