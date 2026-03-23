package multi_lvl_parkinglot;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<SlotType, Double> rates = new HashMap<>();
        rates.put(SlotType.SMALL, 5.0);
        rates.put(SlotType.MEDIUM, 10.0);
        rates.put(SlotType.LARGE, 20.0);
        PricingStrategy pricing = new HourlyPricingStrategy(rates);

        SlotAssignmentStrategy assignment = new NearestEuclideanSlotStrategy();

        ParkingLot parkingLot = new ParkingLot(assignment, pricing);

        Gate gate1 = new Gate("G1", 1, 0, 0);
        parkingLot.addGate(gate1);

        parkingLot.addSlot(new Slot("S1", SlotType.MEDIUM, 1, 10, 10));
        parkingLot.addSlot(new Slot("S2", SlotType.MEDIUM, 1, 50, 50));
        parkingLot.addSlot(new Slot("S3", SlotType.SMALL, 2, 5, 5));

        parkingLot.getParkingLotStatus(null);

        Vehicle car1 = new Vehicle("ABC-123", VehicleType.CAR);
        Ticket ticket1 = parkingLot.park(car1, gate1);


        if (ticket1 != null) {
            long twoHoursAgo = System.currentTimeMillis() - (2 * 60 * 60 * 1000) - 1000;
            ticket1.setEntryTime(twoHoursAgo);
            parkingLot.exit(ticket1);
        }

        parkingLot.getParkingLotStatus(null);
    }
}