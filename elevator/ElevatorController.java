import java.util.ArrayList;
import java.util.List;

public class ElevatorController {
    private List<ElevatorCar> elevators;
    private ElevatorSelectionStrategy selectionStrategy;

    public ElevatorController(ElevatorSelectionStrategy selectionStrategy) {
        this.elevators = new ArrayList<>();
        this.selectionStrategy = selectionStrategy;
    }

    public void addElevator(ElevatorCar car) {
        elevators.add(car);
    }

    public synchronized void requestElevator(int floor, Direction direction) {
        System.out.println("External request from floor " + floor + " to go " + direction);
        ElevatorCar selectedCar = selectionStrategy.selectElevator(elevators, floor, direction);

        if (selectedCar != null) {
            System.out.println("Assigned Elevator " + selectedCar.getId() + " to floor " + floor);
            selectedCar.move(floor);
        } else {
            System.out.println("No available elevators at the moment.");
        }
    }

    public synchronized void handleInternalRequest(ElevatorCar car, int targetFloor) {
        System.out.println("Internal request in Elevator " + car.getId() + " for floor " + targetFloor);
        car.move(targetFloor);
    }

    public synchronized void triggerEmergency() {
        System.out.println("ALARM BUTTON PRESSED! Triggering building-wide emergency protocol.");
        for (ElevatorCar car : elevators) {
            car.stopForEmergency();
        }
    }

    public synchronized void handlePowerOutage() {
        System.out.println("POWER OUTAGE DETECTED! Switching to backup power and returning all cars to ground floor.");
        for (ElevatorCar car : elevators) {
            if (car.getState() != ElevatorState.MAINTENANCE) {
                car.move(0);
                car.openDoor();
                car.setState(ElevatorState.MAINTENANCE);
            }
        }
    }
}