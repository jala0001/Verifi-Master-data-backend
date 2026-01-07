package verifi.verifimasterdatabackend.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import verifi.verifimasterdatabackend.enums.Role;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void testTokenGenerationAndValidation() {
        // Arrange
        String username = "testuser";
        Role role = Role.ADMIN;

        // Act
        String token = jwtService.generateToken(username, role);

        // Assert
        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
        assertTrue(jwtService.validateToken(token), "Token should be valid");
    }

    @Test
    void testExtractUsernameFromToken() {
        // Arrange
        String username = "adminuser";
        Role role = Role.ADMIN;
        String token = jwtService.generateToken(username, role);

        // Act
        String extractedUsername = jwtService.getUsernameFromToken(token);

        // Assert
        assertEquals(username, extractedUsername, "Extracted username should match original");
    }

    @Test
    void testExtractRoleFromToken() {
        // Arrange
        String username = "regularuser";
        Role role = Role.USER;
        String token = jwtService.generateToken(username, role);

        // Act
        Role extractedRole = jwtService.getRoleFromToken(token);

        // Assert
        assertEquals(role, extractedRole, "Extracted role should match original");
    }

    @Test
    void testInvalidTokenValidation() {
        // Arrange
        String invalidToken = "this.is.not.a.valid.jwt.token";

        // Act & Assert
        assertFalse(jwtService.validateToken(invalidToken), "Invalid token should not validate");
    }
}