public interface EligibilityRule {
    void evaluate(StudentProfile s, java.util.List<String> reasons);
}
