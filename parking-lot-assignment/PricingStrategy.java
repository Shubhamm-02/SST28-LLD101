
public interface PricingStrategy {
    double calculateFee(Ticket ticket, long exitTimeMillis);
}