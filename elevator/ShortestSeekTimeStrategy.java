import java.util.List;

public class ShortestSeekTimeStrategy implements ElevatorSelectionStrategy {
    @Override
    public ElevatorCar selectElevator(List<ElevatorCar> elevators, int targetFloor, Direction direction) {
        ElevatorCar selectedCar = null;
        int minDistance = Integer.MAX_VALUE;

        for (ElevatorCar car : elevators) {
            if (car.getState() == ElevatorState.MAINTENANCE) {
                continue;
            }

            int distance = Math.abs(car.getCurrentFloor() - targetFloor);
            
            if (car.getState() == ElevatorState.IDLE) {
                if (distance < minDistance) {
                    minDistance = distance;
                    selectedCar = car;
                }
            } else if (car.getState() == ElevatorState.UP && direction == Direction.UP && car.getCurrentFloor() <= targetFloor) {
                if (distance < minDistance) {
                    minDistance = distance;
                    selectedCar = car;
                }
            } else if (car.getState() == ElevatorState.DOWN && direction == Direction.DOWN && car.getCurrentFloor() >= targetFloor) {
                if (distance < minDistance) {
                    minDistance = distance;
                    selectedCar = car;
                }
            }
        }

        return selectedCar;
    }
}