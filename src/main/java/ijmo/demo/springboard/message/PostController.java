package ijmo.demo.springboard.message;

import ijmo.demo.springboard.CannotCreateException;
import ijmo.demo.springboard.CannotDeleteException;
import ijmo.demo.springboard.CannotUpdateException;
import ijmo.demo.springboard.UnauthenticatedException;
import ijmo.demo.springboard.handler.BaseController;
import ijmo.demo.springboard.session.UserSession;
import ijmo.demo.springboard.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@Controller
@RequestMapping("/posts")
public class PostController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    private static final String VIEW_CREATE_OR_UPDATE_POST_FORM = "post/createOrUpdatePostForm";
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @InitBinder("message")
    public void initMessageBinder(WebDataBinder dataBinder) {
        dataBinder.setValidator(new MessageValidator(true));
    }

    @GetMapping("")
    public String showPostList(@ModelAttribute UserSession userSession, Model model) {
        model.addAttribute("posts", postService.findAll());
        return "post/postList";
    }

    @GetMapping("/{postId}")
    public String showPost(@PathVariable("postId") int postId, @ModelAttribute UserSession userSession, Model model) {
        postService.findPostById(postId)
                .map(model::addAttribute)
                .orElseThrow(EntityNotFoundException::new);
        return "post/postDetails";
    }

    @GetMapping("/new")
    public String initCreationForm(@ModelAttribute UserSession userSession, Model model) throws Exception {
        userSession.getUser().orElseThrow(UnauthenticatedException::new);
        model.addAttribute(new Message());
        return VIEW_CREATE_OR_UPDATE_POST_FORM;
    }

    @PostMapping("/new")
    public String processCreationForm(@ModelAttribute UserSession userSession, @Valid @ModelAttribute Message message, BindingResult result, Model model) throws Exception {
        if (result.hasErrors()) {
            model.addAttribute(message);
            return VIEW_CREATE_OR_UPDATE_POST_FORM;
        }
        User user = userSession.getUser().orElseThrow(UnauthenticatedException::new);
        return postService.addPost(message, user)
                .map(post -> "redirect:/posts/" + post.getId())
                .orElseThrow(CannotCreateException::new);
    }

    @GetMapping("/{postId}/edit")
    public String initUpdatePostForm(@PathVariable("postId") int postId, @ModelAttribute UserSession userSession, Model model) throws Exception {
        userSession.getUser().orElseThrow(UnauthenticatedException::new);
        postService.findPostById(postId)
                .map(post -> model.addAttribute(post.getMessage()))
                .orElseThrow(EntityNotFoundException::new);
        return "post/createOrUpdatePostForm";
    }

    @PostMapping("/{postId}/edit")
    public String processUpdatePostForm(@PathVariable("postId") int postId, @ModelAttribute UserSession userSession, @Valid @ModelAttribute Message message, BindingResult result, Model model) throws Exception {
        if (result.hasErrors()) {
            model.addAttribute(message);
            return VIEW_CREATE_OR_UPDATE_POST_FORM;
        }
        User user = userSession.getUser().orElseThrow(UnauthenticatedException::new);
        return postService.updatePost(message, postId, user)
                .map(post -> "redirect:/posts/{postId}")
                .orElseThrow(CannotUpdateException::new);
    }

    @PostMapping("/{postId}/delete")
    public String processDeletion(@PathVariable("postId") int postId, @ModelAttribute UserSession userSession) throws Exception {
        User user = userSession.getUser().orElseThrow(UnauthenticatedException::new);
        boolean result = postService.deletePost(postId, user);
        if (result) {
            return "redirect:/posts";
        }
        throw new CannotDeleteException();
    }
}