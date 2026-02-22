import java.util.*;

public class EligibilityEngine {
    private final FakeEligibilityStore store;
    private final List<EligibilityRule> rules;

    public EligibilityEngine(FakeEligibilityStore store) {
        this.store = store;
        this.rules = Arrays.asList(
                new DisciplinaryRule(),
                new CgrRule(),
                new AttendanceRule(),
                new CreditsRule());
    }

    public void runAndPrint(StudentProfile s) {
        ReportPrinter p = new ReportPrinter();
        EligibilityResult r = evaluate(s);
        p.print(s, r);
        store.save(s.rollNo, r.status);
    }

    public EligibilityResult evaluate(StudentProfile s) {
        List<String> reasons = new ArrayList<>();
        for (EligibilityRule rule : rules) {
            rule.evaluate(s, reasons);
            if (!reasons.isEmpty())
                break; // mimics original else-if behavior
        }

        String status = reasons.isEmpty() ? "ELIGIBLE" : "NOT_ELIGIBLE";
        return new EligibilityResult(status, reasons);
    }
}
