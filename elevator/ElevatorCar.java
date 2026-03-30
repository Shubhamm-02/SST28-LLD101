public class ElevatorCar {
    private String id;
    private int currentFloor;
    private ElevatorState state;
    private double weightCapacity;
    private double currentWeight;
    private boolean isDoorOpen;

    public ElevatorCar(String id, double weightCapacity) {
        this.id = id;
        this.weightCapacity = weightCapacity;
        this.currentFloor = 0;
        this.state = ElevatorState.IDLE;
        this.currentWeight = 0.0;
        this.isDoorOpen = false;
    }

    public ElevatorCar(String id) {
        this(id, 700.0);
    }

    public String getId() { return id; }
    public int getCurrentFloor() { return currentFloor; }
    public ElevatorState getState() { return state; }
    public void setState(ElevatorState state) { this.state = state; }

    public void addWeight(double weight) {
        this.currentWeight += weight;
    }

    public void removeWeight(double weight) {
        this.currentWeight -= weight;
        if (this.currentWeight < 0) this.currentWeight = 0;
    }

    public boolean isOverweight() {
        return currentWeight > weightCapacity;
    }

    public void handleOverweight() {
        System.out.println("Elevator " + id + " is Overweight! Playing warning sound.");
        openDoor();
    }

    public void move(int targetFloor) {
        if (state == ElevatorState.MAINTENANCE) return;
        
        if (isOverweight()) {
            handleOverweight();
            return;
        }

        closeDoor();
        this.state = (targetFloor > currentFloor) ? ElevatorState.UP : ElevatorState.DOWN;
        System.out.println("Elevator " + id + " moving " + state + " from floor " + currentFloor + " to " + targetFloor);
        this.currentFloor = targetFloor;
        this.state = ElevatorState.IDLE;
        openDoor();
    }

    public void openDoor() {
        this.isDoorOpen = true;
        System.out.println("Elevator " + id + " door opened.");
    }

    public void closeDoor() {
        this.isDoorOpen = false;
        System.out.println("Elevator " + id + " door closed.");
    }

    public void stopForEmergency() {
        this.state = ElevatorState.IDLE;
        this.isDoorOpen = true;
        System.out.println("EMERGENCY! Elevator " + id + " stopped at floor " + currentFloor);
    }
}