package multi_lvl_parkinglot;
import java.util.List;

public class NearestEuclideanSlotStrategy implements SlotAssignmentStrategy {
    
    private SlotType getRequiredSlotType(VehicleType vehicleType) {
        switch (vehicleType) {
            case TWO_WHEELER: return SlotType.SMALL;
            case CAR: return SlotType.MEDIUM;
            case BUS: return SlotType.LARGE;
            default: throw new IllegalArgumentException("Unknown vehicle type");
        }
    }

    @Override
    public Slot findNearestAvailableSlot(Gate entryGate, List<Slot> slots, VehicleType vehicleType) throws Exception {
        SlotType requiredType = getRequiredSlotType(vehicleType);
        
        Slot nearestSlot = null;
        double minDistance = Double.MAX_VALUE;

        for (Slot slot : slots) {
            if (!slot.isOccupied() && slot.getType() == requiredType) {
                double distance = slot.calculateDistanceTo(entryGate);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestSlot = slot;
                }
            }
        }

        if (nearestSlot == null) {
            throw new Exception("Parking Lot is Full for type: " + requiredType);
        }
        return nearestSlot;
    }
}