public class DriverAllocator implements AllocationService {
    public String allocate(String studentId) {
        // fake deterministic driver
        return "DRV-17";
    }
}
