public class Main {
    public static void main(String[] args) {
        ElevatorSelectionStrategy strategy = new ShortestSeekTimeStrategy();
        ElevatorController controller = new ElevatorController(strategy);

        ElevatorCar car1 = new ElevatorCar("A", 700.0);
        ElevatorCar car2 = new ElevatorCar("B", 1000.0); 
        ElevatorCar car3 = new ElevatorCar("C"); 

        controller.addElevator(car1);
        controller.addElevator(car2);
        controller.addElevator(car3);

        car3.setState(ElevatorState.MAINTENANCE);

        ExternalPanel floor5Panel = new ExternalPanel(5, controller);
        InternalPanel car1Panel = new InternalPanel(car1, controller);

        floor5Panel.pressUp();

        car1.addWeight(800.0);
        car1Panel.pressFloorButton(10);

        car1.removeWeight(200.0);
        car1Panel.pressFloorButton(10);

        car1Panel.pressAlarm();
    }
}