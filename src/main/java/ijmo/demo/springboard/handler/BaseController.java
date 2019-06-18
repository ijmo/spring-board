package ijmo.demo.springboard.handler;

import ijmo.demo.springboard.session.UserSession;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

@SessionAttributes("userSession")
public class BaseController {

    @ModelAttribute("userSession")
    public UserSession getUserSession() {
        return new UserSession();
    }
}
