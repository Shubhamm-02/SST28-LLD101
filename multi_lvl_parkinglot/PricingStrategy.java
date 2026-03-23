package multi_lvl_parkinglot;
public interface PricingStrategy {
    double calculateFee(Ticket ticket, long exitTimeMillis);
}