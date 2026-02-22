import java.util.*;

public class OnboardingService {
    private final InputParser parser;
    private final StudentValidator validator;
    private final StudentRepository repository;
    private final OnboardingPresenter presenter;

    public OnboardingService(InputParser parser, StudentValidator validator, StudentRepository repository,
            OnboardingPresenter presenter) {
        this.parser = parser;
        this.validator = validator;
        this.repository = repository;
        this.presenter = presenter;
    }

    public void registerFromRawInput(String raw) {
        presenter.printInput(raw);

        Map<String, String> data = parser.parse(raw);
        List<String> errors = validator.validate(data);

        if (!errors.isEmpty()) {
            presenter.printErrors(errors);
            return;
        }

        String id = IdUtil.nextStudentId(repository.count());
        StudentRecord rec = new StudentRecord(
                id,
                data.getOrDefault("name", ""),
                data.getOrDefault("email", ""),
                data.getOrDefault("phone", ""),
                data.getOrDefault("program", ""));

        repository.save(rec);
        presenter.printSuccess(id, repository.count(), rec);
    }
}
