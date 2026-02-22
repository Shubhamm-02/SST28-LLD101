import java.util.*;

public class RoomFeeRule implements FeeRule {
    private final Map<Integer, Double> prices;

    public RoomFeeRule(Map<Integer, Double> prices) {
        this.prices = prices;
    }

    @Override
    public Money calculate(BookingRequest req) {
        double price = prices.getOrDefault(req.roomType, 16000.0); // Default to DELUXE price from legacy
        return new Money(price);
    }
}
