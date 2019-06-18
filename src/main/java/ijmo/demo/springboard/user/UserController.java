package ijmo.demo.springboard.user;

import ijmo.demo.springboard.handler.BaseController;
import ijmo.demo.springboard.session.UserSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController extends BaseController {

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
    public String processLoginForm(@ModelAttribute UserSession userSession, User user) {
        userSession.setUser(userService.loginOrSignUp(user.getUsername()));

        return "redirect:/";
    }


    @GetMapping("/logout")
    public String processLogout(@ModelAttribute UserSession userSession) {
        userSession.kickUser();
        System.out.println("# User Logged out");

        return "redirect:/";
    }
}