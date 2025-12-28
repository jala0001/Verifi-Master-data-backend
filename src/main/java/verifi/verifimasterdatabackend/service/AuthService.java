package verifi.verifimasterdatabackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import verifi.verifimasterdatabackend.dto.LoginRequest;
import verifi.verifimasterdatabackend.dto.LoginResponse;
import verifi.verifimasterdatabackend.entity.User;
import verifi.verifimasterdatabackend.enums.Role;
import verifi.verifimasterdatabackend.repository.UserRepository;
import verifi.verifimasterdatabackend.util.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse login(LoginRequest request) {
        System.out.println("=== LOGIN DEBUG ===");
        System.out.println("Username from request: " + request.getUsername());
        System.out.println("Password from request: " + request.getPassword());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        System.out.println("User found: " + user.getUsername());
        System.out.println("Password hash from DB: " + user.getPasswordHash());

        // Verificer password
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        System.out.println("Password matches: " + matches);

        if (!matches) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generer JWT token
        String token = jwtService.generateToken(user.getUsername(), Role.valueOf(user.getRole()));

        return new LoginResponse(token, user.getUsername(), user.getRole());
    }
}