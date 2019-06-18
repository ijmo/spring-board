package ijmo.demo.springboard.session;

import ijmo.demo.springboard.user.User;

import javax.servlet.http.HttpSession;
import java.util.Optional;

public class SessionUtils {
    public static String USER_SESSION_KEY = "user";

    public static Optional<User> getUserFrom(HttpSession session) {
        return Optional.ofNullable((User) session.getAttribute(USER_SESSION_KEY));
    }

    public static void clear(HttpSession session) {
        session.invalidate();
    }
}