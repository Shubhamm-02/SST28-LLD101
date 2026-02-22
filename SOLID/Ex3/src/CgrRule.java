public class CgrRule implements EligibilityRule {
    @Override
    public void evaluate(StudentProfile s, java.util.List<String> reasons) {
        if (s.cgr < 8.0) {
            reasons.add("CGR below 8.0");
        }
    }
}
