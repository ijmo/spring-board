package ijmo.demo.springboard.handler;

import ijmo.demo.springboard.session.UserSession;
import ijmo.demo.springboard.user.CurrentUser;
import ijmo.demo.springboard.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@Slf4j
@Controller
public class HomeController extends BaseController {

    @GetMapping("")
    public String home(@CurrentUser User user, Model model) {
        Optional.ofNullable(user).ifPresent(model::addAttribute);
        return "home";
    }

    @GetMapping("/login")
    public String processLoginForm(@ModelAttribute UserSession userSession, Model model) {
        model.addAttribute(new User());
        return "login";
    }
}