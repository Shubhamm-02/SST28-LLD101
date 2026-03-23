package multi_lvl_parkinglot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParkingLot {
    private List<Slot> slots;
    private List<Gate> gates;
    private SlotAssignmentStrategy assignmentStrategy;
    private PricingStrategy pricingStrategy;

    public ParkingLot(SlotAssignmentStrategy assignmentStrategy, PricingStrategy pricingStrategy) {
        this.slots = new ArrayList<>();
        this.gates = new ArrayList<>();
        this.assignmentStrategy = assignmentStrategy;
        this.pricingStrategy = pricingStrategy;
    }

    public void addSlot(Slot slot) { slots.add(slot); }
    public void addGate(Gate gate) { gates.add(gate); }

    public Ticket park(Vehicle vehicle, Gate entryGate) {
        try {
            System.out.println("Attempting to park " + vehicle.getType() + " at Gate " + entryGate.getId());
            Slot assignedSlot = assignmentStrategy.findNearestAvailableSlot(entryGate, slots, vehicle.getType());
            
            assignedSlot.occupy();
            Ticket ticket = new Ticket(vehicle, assignedSlot);
            System.out.println("Assigned Slot: " + assignedSlot.getId() + " on Floor " + assignedSlot.getFloor());
            return ticket;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public double exit(Ticket ticket) {
        ticket.getSlot().free();
        long currentTime = System.currentTimeMillis();
        double fee = pricingStrategy.calculateFee(ticket, currentTime);
        
        System.out.println("Vehicle " + ticket.getVehicle().getLicensePlate() + 
                           " exited. Slot " + ticket.getSlot().getId() + " is now free. Fee: $" + fee);
        return fee;
    }

    public void getParkingLotStatus(SlotType optionalFilterType) {
        Map<SlotType, Long> availableCounts = slots.stream()
            .filter(slot -> !slot.isOccupied())
            .filter(slot -> optionalFilterType == null || slot.getType() == optionalFilterType)
            .collect(Collectors.groupingBy(Slot::getType, Collectors.counting()));

        System.out.println("\n--- Parking Lot Status ---");
        if (availableCounts.isEmpty()) {
            System.out.println("No slots available.");
        } else {
            availableCounts.forEach((type, count) -> 
                System.out.println(type + " SLOTS available: " + count));
        }
        System.out.println("--------------------------\n");
    }
}