package ijmo.demo.springboard.session;

import ijmo.demo.springboard.user.User;

import javax.servlet.http.HttpSession;
import java.util.Optional;

public class SessionUtils {
    public static final String USER_SESSION_KEY = "userSession";

    public static Optional<User> getUserFrom(HttpSession session) {
        UserSession userSession = (UserSession) session.getAttribute(SessionUtils.USER_SESSION_KEY);
        if (userSession != null) {
            return userSession.getUser();
        }
        return Optional.empty();
    }
    public static void clear(HttpSession session) {
        session.invalidate();
    }
}