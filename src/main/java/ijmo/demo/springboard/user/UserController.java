package ijmo.demo.springboard.user;

import ijmo.demo.springboard.handler.BaseController;
import ijmo.demo.springboard.session.UserSession;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

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
    public String processLoginForm(@ModelAttribute UserSession userSession, @Valid @NotBlank String username, BindingResult result) {
        userSession.setUser(userService.loginOrSignUp(username));
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String processLogout(@ModelAttribute UserSession userSession) {
        userSession.kickUser();
        return "redirect:/";
    }
}