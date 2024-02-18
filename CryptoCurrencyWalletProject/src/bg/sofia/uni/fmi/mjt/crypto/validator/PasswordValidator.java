package bg.sofia.uni.fmi.mjt.crypto.validator;

import java.util.regex.Pattern;

public class PasswordValidator {
    private static final String PASSWORD_REGEX = "^(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[-.#%!@$?^&*]).{12,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private static final String PASSWORD_RESTRICTIONS = """
            Password must contain:
            - at least 12 characters
            - at least one upper case letter
            - at least one digit
            - at least one special character from the following: .#%!@-$?^&*""";

    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static String getPasswordRestrictions() {
        return PASSWORD_RESTRICTIONS;
    }

}
