import java.util.List;

public interface ElevatorSelectionStrategy {
    ElevatorCar selectElevator(List<ElevatorCar> elevators, int targetFloor, Direction direction);
}