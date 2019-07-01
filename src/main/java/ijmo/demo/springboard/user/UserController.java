package ijmo.demo.springboard.user;

import ijmo.demo.springboard.UnauthenticatedException;
import ijmo.demo.springboard.handler.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@Controller
public class UserController extends BaseController {

    private final String VIEW_SIGN_UP_FORM = "user/signUpForm";
    private final String VIEW_UPDATE_USER_PROFILE_FORM = "user/updateUserProfileForm";
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String initCreationForm(Model model) {
        model.addAttribute(new User());
        return VIEW_SIGN_UP_FORM;
    }

    @PostMapping("/signup")
    public String processCreationForm(@Valid @ModelAttribute User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(user);
            return VIEW_SIGN_UP_FORM;
        }
        userService.addUser(user);
        return "redirect:/";
    }

    @GetMapping("/users/{username}/profile")
    public String initUpdateUserSettingsForm(@PathVariable("username") String username, @CurrentUser User currentUser, Model model) throws Exception {
        Optional.ofNullable(currentUser).orElseThrow(UnauthenticatedException::new);
        User user = userService.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        model.addAttribute(user);
        if (StringUtils.equals(username, currentUser.getUsername())) {
            return VIEW_UPDATE_USER_PROFILE_FORM;
        }
        return "user/profileDetail";
    }

    @PostMapping("/users/{username}/profile")
    public String processUpdateUserSettingsForm(@PathVariable("username") String username, @CurrentUser User currentUser, @Valid @ModelAttribute User user, BindingResult result, Model model) throws Exception {

        return VIEW_UPDATE_USER_PROFILE_FORM;
    }

    @GetMapping("/users/find")
    public String initFindForm() {
        return "";
    }
}