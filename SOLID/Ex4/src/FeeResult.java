public class FeeResult {
    public final Money monthly;
    public final Money deposit;

    public FeeResult(Money monthly, Money deposit) {
        this.monthly = monthly;
        this.deposit = deposit;
    }
}
