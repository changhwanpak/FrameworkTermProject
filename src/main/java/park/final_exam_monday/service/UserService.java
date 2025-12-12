package park.final_exam_monday.service;

import park.final_exam_monday.entity.User;
import park.final_exam_monday.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public User registerUser(String username, String rawPassword, boolean admin) {
        if (userRepository.findByUsername(username).isPresent())
            throw new RuntimeException("이미 존재하는 사용자명");

        User u = new User();
        u.setUsername(username);
        u.setPassword(encoder.encode(rawPassword));
        u.setRole(admin ? "ROLE_ADMIN" : "ROLE_USER");
        return userRepository.save(u);
    }
}
