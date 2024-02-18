package bg.sofia.uni.fmi.mjt.crypto.validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordValidatorTest {
    @Test
    public void testIsValidPasswordWithValidPasswordReturnsTrue() {
        assertTrue(PasswordValidator.isValidPassword("ValidPas$word123"),
            "The password is valid, but the validator returned false");
    }

    @Test
    public void testIsValidPasswordWithNullPasswordReturnsFalse() {
        assertFalse(PasswordValidator.isValidPassword(null),
            "The password is null, validator should return false");
    }

    @Test
    public void testIsValidPasswordWithShortPasswordReturnsFalse() {
        assertFalse(PasswordValidator.isValidPassword("ShortPas$1"),
            "The password is too short, validator should return false");
    }

    @Test
    public void testIsValidPasswordWithoutNumberReturnsFalse() {
        assertFalse(PasswordValidator.isValidPassword("PasswordWith*utNumbers"),
            "The password does not contain a number, validator should return false");
    }

    @Test
    public void testIsValidPasswordWithoutUpperCaseLetterReturnsFalse() {
        assertFalse(PasswordValidator.isValidPassword("passwordwithoutupperca$e1"),
            "The password does not contain an upper case letter, validator should return false");
    }

    @Test
    public void testIsValidPasswordWithoutSpecialSymbolReturnsFalse() {
        assertFalse(PasswordValidator.isValidPassword("withoutSpecialSymbol123"),
            "The password does not contain a special symbol, validator should return false");
    }

    @Test
    public void testGetPasswordRestrictions() {
        String expected = """
            Password must contain:
            - at least 12 characters
            - at least one upper case letter
            - at least one digit
            - at least one special character from the following: .#%!@-$?^&*""";
        assertEquals(expected, PasswordValidator.getPasswordRestrictions(),
            "The password restrictions are not as expected");
    }
}