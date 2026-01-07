package verifi.verifimasterdatabackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import verifi.verifimasterdatabackend.entity.User;
import verifi.verifimasterdatabackend.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DefaultAdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        // Tjek om der allerede er brugere i systemet
        if (userRepository.count() == 0) {
            // Opret default admin bruger
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("verifi2025"));
            admin.setRole("ADMIN");

            userRepository.save(admin);

            System.out.println("═══════════════════════════════════════════════════════");
            System.out.println("   DEFAULT ADMIN USER CREATED");
            System.out.println("   Username: admin");
            System.out.println("   Password: verifi2025");
            System.out.println("   ");
            System.out.println("   IMPORTANT: Change this password after first login!");
            System.out.println("═══════════════════════════════════════════════════════");
        } else {
            System.out.println("Users already exist in database. Skipping default admin creation.");
        }
    }
}