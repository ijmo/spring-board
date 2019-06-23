package ijmo.demo.springboard.handler;

import ijmo.demo.springboard.session.SessionUtils;
import ijmo.demo.springboard.session.UserSession;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

@SessionAttributes(SessionUtils.USER_SESSION_KEY)
@Validated
public class BaseController {

    @ModelAttribute(SessionUtils.USER_SESSION_KEY)
    public UserSession userSession() {
        return new UserSession();
    }
}
