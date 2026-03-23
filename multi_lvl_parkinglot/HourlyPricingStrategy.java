package multi_lvl_parkinglot;
import java.util.Map;

public class HourlyPricingStrategy implements PricingStrategy {
    private Map<SlotType, Double> hourlyRates;

    public HourlyPricingStrategy(Map<SlotType, Double> hourlyRates) {
        this.hourlyRates = hourlyRates;
    }

    @Override
    public double calculateFee(Ticket ticket, long exitTimeMillis) {
        long durationMillis = exitTimeMillis - ticket.getEntryTime();
        double hours = Math.ceil(durationMillis / (1000.0 * 60 * 60));
        if (hours == 0) hours = 1; 
        
        double rate = hourlyRates.getOrDefault(ticket.getSlot().getType(), 10.0);
        return hours * rate;
    }
}