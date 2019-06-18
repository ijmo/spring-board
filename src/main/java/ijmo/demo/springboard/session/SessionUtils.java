package ijmo.demo.springboard.session;

import javax.servlet.http.HttpSession;

public class SessionUtils {
    public static void clear(HttpSession session) {
        session.invalidate();
    }
}