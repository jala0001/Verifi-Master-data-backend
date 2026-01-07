package verifi.verifimasterdatabackend.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void testPasswordHashingAndValidation() {
        // Arrange
        String rawPassword = "testPassword123";

        // Act
        String hashedPassword = passwordEncoder.encode(rawPassword);
        boolean matches = passwordEncoder.matches(rawPassword, hashedPassword);
        boolean wrongPasswordMatches = passwordEncoder.matches("wrongPassword", hashedPassword);

        // Assert
        assertNotEquals(rawPassword, hashedPassword, "Password should be hashed, not stored in plain text");
        assertTrue(hashedPassword.startsWith("$2a$"), "BCrypt hash should start with $2a$");
        assertTrue(matches, "Correct password should match the hash");
        assertFalse(wrongPasswordMatches, "Wrong password should not match the hash");
    }
}