public interface FeeRule {
    Money calculate(BookingRequest req);
}
