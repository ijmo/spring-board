package ijmo.demo.springboard.handler;

import ijmo.demo.springboard.session.SessionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class HomeController {
    @GetMapping("")
    public String home(HttpSession session, Model model) {
        SessionUtils.getUserFrom(session).ifPresent(user -> {
            System.out.println("****** user: " + user.getUsername());
            model.addAttribute(user);
        });
        return "home";
    }
}
