public class InternalPanel {
    private ElevatorCar car;
    private ElevatorController controller;

    public InternalPanel(ElevatorCar car, ElevatorController controller) {
        this.car = car;
        this.controller = controller;
    }

    public void pressFloorButton(int floor) {
        controller.handleInternalRequest(car, floor);
    }

    public void pressOpenDoor() {
        car.openDoor();
    }

    public void pressCloseDoor() {
        car.closeDoor();
    }

    public void pressAlarm() {
        controller.triggerEmergency();
    }
}