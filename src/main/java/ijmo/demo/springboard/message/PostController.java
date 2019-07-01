package ijmo.demo.springboard.message;

import ijmo.demo.springboard.CannotCreateException;
import ijmo.demo.springboard.CannotDeleteException;
import ijmo.demo.springboard.CannotUpdateException;
import ijmo.demo.springboard.UnauthenticatedException;
import ijmo.demo.springboard.handler.BaseController;
import ijmo.demo.springboard.user.CurrentUser;
import ijmo.demo.springboard.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/posts")
public class PostController extends BaseController {
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
    public String showPostList(Model model) {
        model.addAttribute("posts", postService.findAll());
        return "post/postList";
    }

    @GetMapping("/{postId}")
    public String showPost(@PathVariable("postId") int postId, Model model) {
        postService.findPostById(postId)
                .map(model::addAttribute)
                .orElseThrow(EntityNotFoundException::new);
        return "post/postDetails";
    }

    @GetMapping("/new")
    public String initCreationForm(@CurrentUser User user, Model model) throws Exception {
        Optional.ofNullable(user).orElseThrow(UnauthenticatedException::new);
        model.addAttribute(new Message());
        return VIEW_CREATE_OR_UPDATE_POST_FORM;
    }

    @PostMapping("/new")
    public String processCreationForm(@CurrentUser User user, @Valid @ModelAttribute Message message, BindingResult result, Model model) throws Exception {
        if (result.hasErrors()) {
            model.addAttribute(message);
            return VIEW_CREATE_OR_UPDATE_POST_FORM;
        }
        Optional.ofNullable(user).orElseThrow(UnauthenticatedException::new);
        return postService.addPost(message, user)
                .map(post -> "redirect:/posts/" + post.getId())
                .orElseThrow(CannotCreateException::new);
    }

    @GetMapping("/{postId}/edit")
    public String initUpdatePostForm(@PathVariable("postId") int postId, @CurrentUser User user, Model model) throws Exception {
        Optional.ofNullable(user).orElseThrow(UnauthenticatedException::new);
        postService.findPostById(postId)
                .map(post -> model.addAttribute(post.getMessage()))
                .orElseThrow(EntityNotFoundException::new);
        return VIEW_CREATE_OR_UPDATE_POST_FORM;
    }

    @PostMapping("/{postId}/edit")
    public String processUpdatePostForm(@PathVariable("postId") int postId, @CurrentUser User user, @Valid @ModelAttribute Message message, BindingResult result, Model model) throws Exception {
        if (result.hasErrors()) {
            model.addAttribute(message);
            return VIEW_CREATE_OR_UPDATE_POST_FORM;
        }
        Optional.ofNullable(user).orElseThrow(UnauthenticatedException::new);
        return postService.updatePost(message, postId, user)
                .map(post -> "redirect:/posts/{postId}")
                .orElseThrow(CannotUpdateException::new);
    }

    @PostMapping("/{postId}/delete")
    public String processDeletion(@PathVariable("postId") int postId, @CurrentUser User user) throws Exception {
        Optional.ofNullable(user).orElseThrow(UnauthenticatedException::new);
        boolean result = postService.deletePost(postId, user);
        if (result) {
            return "redirect:/posts";
        }
        throw new CannotDeleteException();
    }
}