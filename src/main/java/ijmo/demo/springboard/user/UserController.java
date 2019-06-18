package ijmo.demo.springboard.user;

import ijmo.demo.springboard.session.SessionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/new")
    public String initCreationForm() {
        return "";
    }

    @GetMapping("/find")
    public String initFindForm() {
        return "";
    }

    @PostMapping("/login")
    public String processLoginForm(User user, HttpSession httpSession) {
        httpSession.setAttribute(SessionUtils.USER_SESSION_KEY, userService.loginOrSignUp(user.getUsername()));

        return "redirect:/";
    }


    @GetMapping("/logout")
    public String processLogout(HttpSession httpSession) {
        SessionUtils.clear(httpSession);
        System.out.println("# User Logged out");

        return "redirect:/";
    }
}