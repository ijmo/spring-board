package ijmo.demo.springboard.handler;

import ijmo.demo.springboard.session.UserSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class HomeController extends BaseController {
    @GetMapping("")
    public String home(@ModelAttribute UserSession userSession, Model model) {
        userSession.getUser().ifPresent(user -> {
            System.out.println("****** user: " + user.getUsername());
            model.addAttribute(user);
        });
        return "home";
    }
}
