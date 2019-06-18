package ijmo.demo.springboard.session;

import ijmo.demo.springboard.user.User;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
public class UserSession {
    private User user;

    public boolean isLoggedIn() {
        return user != null;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void kickUser() {
        user = null;
    }

    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }
}