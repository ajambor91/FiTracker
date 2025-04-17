package aj.FiTracker.FiTracker.Validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.Set;

public class PasswordValidator implements ConstraintValidator<PasswordValidatorAnnotation, char[]> {
    private static final Set<Character> SPECIAL_CHARACTERS = new HashSet<>();

    static {
        String specials = "!@#$%^&*+=?<>.,:;\"'\\|/~`";
        for (char c : specials.toCharArray()) {
            SPECIAL_CHARACTERS.add(c);
        }
    }

    private final int minimumPasswwordLen = 8;

    @Override
    public boolean isValid(char[] password, ConstraintValidatorContext context) {
        if (password == null || password.length < minimumPasswwordLen) {
            return false;
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        for (char c : password) {
            if (!hasUpper && Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (!hasLower && Character.isLowerCase(c)) {
                hasLower = true;
            } else if (!hasDigit && Character.isDigit(c)) {
                hasDigit = true;
            } else if (!hasSpecial && SPECIAL_CHARACTERS.contains(c)) {
                hasSpecial = true;
            }
            if (hasUpper && hasLower && hasDigit && hasSpecial) {
                break;
            }
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
