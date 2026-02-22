public class Main {
    public static void main(String[] args) {
        System.out.println("=== Student Onboarding ===");

        FakeDb db = new FakeDb();
        InputParser parser = new InputParser();
        StudentValidator validator = new StudentValidator();
        OnboardingPresenter presenter = new OnboardingPresenter();

        OnboardingService svc = new OnboardingService(parser, validator, db, presenter);

        String raw = "name=Riya;email=riya@sst.edu;phone=9876543210;program=CSE";
        svc.registerFromRawInput(raw);

        System.out.println();
        System.out.println("-- DB DUMP --");
        System.out.print(TextTable.render3(db));
    }
}
