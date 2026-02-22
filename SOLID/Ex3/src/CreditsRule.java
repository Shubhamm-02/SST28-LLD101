public class CreditsRule implements EligibilityRule {
    @Override
    public void evaluate(StudentProfile s, java.util.List<String> reasons) {
        if (s.earnedCredits < 20) {
            reasons.add("credits below 20");
        }
    }
}
