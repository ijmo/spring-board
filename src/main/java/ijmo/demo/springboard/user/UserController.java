package ijmo.demo.springboard.user;

import ijmo.demo.springboard.handler.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class UserController extends BaseController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/new")
    public String initCreationForm() {
        return "";
    }

    @GetMapping("/users/find")
    public String initFindForm() {
        return "";
    }
}