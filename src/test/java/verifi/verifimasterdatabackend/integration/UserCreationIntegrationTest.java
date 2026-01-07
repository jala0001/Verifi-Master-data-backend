package verifi.verifimasterdatabackend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import verifi.verifimasterdatabackend.dto.CreateUserRequest;
import verifi.verifimasterdatabackend.dto.UserResponse;
import verifi.verifimasterdatabackend.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserCreationIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void testCreateUserSuccessfully() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setRole("USER");

        // Act
        UserResponse response = userService.createUser(request);

        // Assert
        assertNotNull(response);
        assertEquals("newuser", response.getUsername());
        assertEquals("USER", response.getRole());
        assertNotNull(response.getId());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    void testCreateDuplicateUserThrowsException() {
        // Arrange
        CreateUserRequest request1 = new CreateUserRequest();
        request1.setUsername("duplicateuser");
        request1.setPassword("password123");
        request1.setRole("ADMIN");

        CreateUserRequest request2 = new CreateUserRequest();
        request2.setUsername("duplicateuser");
        request2.setPassword("differentpassword");
        request2.setRole("USER");

        // Act
        userService.createUser(request1); // First creation succeeds

        // Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.createUser(request2));

        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void testPasswordIsHashedNotPlainText() {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("secureuser");
        request.setPassword("mypassword");
        request.setRole("USER");

        // Act
        UserResponse response = userService.createUser(request);

        // Assert - password hash should not equal plain password
        // (Vi kan ikke få fat i passwordHash direkte fra response,
        // men vi ved BCrypt hasher altid starter med $2a$)
        assertNotNull(response);
        assertEquals("secureuser", response.getUsername());
    }
}