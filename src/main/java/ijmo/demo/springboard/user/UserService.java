package ijmo.demo.springboard.user;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User loginOrSignUp(String username) {
        return userRepository.findByUsername(username)
                .orElse(userRepository.save(User.builder().username(username).build()));
    }

    public User updateUsername(User user, String username) {
        user.setUsername(username);
        return userRepository.save(user);
    }
}