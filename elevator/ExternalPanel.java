public class ExternalPanel {
    private int floor;
    private ElevatorController controller;

    public ExternalPanel(int floor, ElevatorController controller) {
        this.floor = floor;
        this.controller = controller;
    }

    public void pressUp() {
        controller.requestElevator(floor, Direction.UP);
    }

    public void pressDown() {
        controller.requestElevator(floor, Direction.DOWN);
    }
}