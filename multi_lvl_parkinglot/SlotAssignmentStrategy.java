package multi_lvl_parkinglot;
import java.util.List;

public interface SlotAssignmentStrategy {
    Slot findNearestAvailableSlot(Gate entryGate, List<Slot> slots, VehicleType vehicleType) throws Exception;
}