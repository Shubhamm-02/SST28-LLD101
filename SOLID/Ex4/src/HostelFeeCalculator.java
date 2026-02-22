import java.util.*;

public class HostelFeeCalculator {
    private final List<FeeRule> monthlyRules;
    private final Money deposit;

    public HostelFeeCalculator(List<FeeRule> monthlyRules, Money deposit) {
        this.monthlyRules = monthlyRules;
        this.deposit = deposit;
    }

    public FeeResult calculate(BookingRequest req) {
        Money totalMonthly = new Money(0.0);
        for (FeeRule rule : monthlyRules) {
            totalMonthly = totalMonthly.plus(rule.calculate(req));
        }
        return new FeeResult(totalMonthly, deposit);
    }
}