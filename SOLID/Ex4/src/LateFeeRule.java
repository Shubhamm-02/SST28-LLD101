public class LateFeeRule implements FeeRule {
    private final Money amount;

    public LateFeeRule(Money amount) {
        this.amount = amount;
    }

    @Override
    public Money calculate(BookingRequest req) {
        // In a real scenario, this might check a field in req,
        // but for demonstration, we apply it if we want it in the pipeline.
        return amount;
    }
}
