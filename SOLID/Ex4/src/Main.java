import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Hostel Fee Calculator ===");

        // Setup room prices
        Map<Integer, Double> roomPrices = new HashMap<>();
        roomPrices.put(LegacyRoomTypes.SINGLE, 14000.0);
        roomPrices.put(LegacyRoomTypes.DOUBLE, 15000.0);
        roomPrices.put(LegacyRoomTypes.TRIPLE, 12000.0);
        roomPrices.put(LegacyRoomTypes.DELUXE, 16000.0);

        // Setup add-on prices
        Map<AddOn, Double> addOnPrices = new MapBuilder<AddOn, Double>()
                .put(AddOn.MESS, 1000.0)
                .put(AddOn.LAUNDRY, 500.0)
                .put(AddOn.GYM, 300.0)
                .build();

        List<FeeRule> rules = List.of(
                new RoomFeeRule(roomPrices),
                new AddOnFeeRule(addOnPrices),
                new LateFeeRule(new Money(200.0)) // OCP Demonstration: Adding a new rule without touching calculator
        );
        HostelFeeCalculator calc = new HostelFeeCalculator(rules, new Money(5000.00));

        BookingRequest req = new BookingRequest(LegacyRoomTypes.DOUBLE, List.of(AddOn.LAUNDRY, AddOn.MESS));
        FeeResult result = calc.calculate(req);

        // Print receipt
        ReceiptPrinter.print(req, result.monthly, result.deposit);

        // Persist booking
        FakeBookingRepo repo = new FakeBookingRepo();
        String bookingId = "H-" + (7000 + new Random(1).nextInt(1000));
        repo.save(bookingId, req, result.monthly, result.deposit);
    }

    // Small helper for easier map initialization without external libs
    private static class MapBuilder<K, V> {
        private final Map<K, V> map = new HashMap<>();

        public MapBuilder<K, V> put(K key, V value) {
            map.put(key, value);
            return this;
        }

        public Map<K, V> build() {
            return map;
        }
    }
}
