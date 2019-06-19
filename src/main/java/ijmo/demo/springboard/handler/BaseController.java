package ijmo.demo.springboard.handler;

import ijmo.demo.springboard.session.UserSession;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

@SessionAttributes("userSession")
@Validated
public class BaseController {

    @ModelAttribute("userSession")
    public UserSession userSession() {
        return new UserSession();
    }
}
