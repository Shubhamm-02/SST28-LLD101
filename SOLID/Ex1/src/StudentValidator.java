import java.util.*;

public class StudentValidator {
    public List<String> validate(Map<String, String> data) {
        List<String> errors = new ArrayList<>();

        String name = data.getOrDefault("name", "");
        String email = data.getOrDefault("email", "");
        String phone = data.getOrDefault("phone", "");
        String program = data.getOrDefault("program", "");

        if (name.isBlank())
            errors.add("name is required");
        if (email.isBlank() || !email.contains("@"))
            errors.add("email is invalid");
        if (phone.isBlank() || !phone.chars().allMatch(Character::isDigit))
            errors.add("phone is invalid");
        if (!(program.equals("CSE") || program.equals("AI") || program.equals("SWE")))
            errors.add("program is invalid");

        return errors;
    }
}
