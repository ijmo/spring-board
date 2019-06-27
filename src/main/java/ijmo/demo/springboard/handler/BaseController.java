package ijmo.demo.springboard.handler;

import ijmo.demo.springboard.session.SessionUtil;
import ijmo.demo.springboard.session.UserSession;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

@SessionAttributes(SessionUtil.USER_SESSION_KEY)
public class BaseController {

    @ModelAttribute(SessionUtil.USER_SESSION_KEY)
    public UserSession userSession() {
        return new UserSession();
    }
}
