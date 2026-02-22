import java.util.List;

public class EligibilityResult {
    public final String status;
    public final List<String> reasons;

    public EligibilityResult(String status, List<String> reasons) {
        this.status = status;
        this.reasons = reasons;
    }
}
