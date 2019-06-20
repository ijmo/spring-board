package ijmo.demo.springboard.message;

import ijmo.demo.springboard.handler.BaseController;
import ijmo.demo.springboard.session.UserSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class PostController extends BaseController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("")
    public String showPostList(@ModelAttribute UserSession userSession, Model model) {
        userSession.getUser().ifPresent(model::addAttribute);
        model.addAttribute("posts", postService.findAllByIsDeletedOrderByCreatedAtDesc());
        return "post/postList";
    }

    @GetMapping("/{postId}")
    public String showPost(@PathVariable("postId") int postId, @ModelAttribute UserSession userSession, Model model) {
        userSession.getUser().ifPresent(model::addAttribute);
        postService.findPostById(postId).ifPresent(model::addAttribute);
        return "post/postDetails";
    }

    @GetMapping("/new")
    public String initCreationForm(@ModelAttribute UserSession userSession, Model model) {
        return userSession.getUser().map(user -> {
            model.addAttribute("user", user);
            return "post/createOrUpdatePostForm";
        }).orElse("redirect:/posts");
    }

    @PostMapping("/new")
    public String processCreationForm(@ModelAttribute @Valid Message message, @ModelAttribute UserSession userSession, BindingResult result) {
        String path = Optional.ofNullable(result)
                .filter(r -> !r.hasErrors())
                .flatMap(o -> userSession.getUser())
                .flatMap(user -> postService.addPost(message, user))
                .map(post -> "/posts/" + post.getId())
                .orElse("/posts");
        return "redirect:" + path;
    }

    @GetMapping("/{postId}/edit")
    public String initUpdatePostForm(@PathVariable("postId") int postId, @ModelAttribute UserSession userSession, ModelMap model) {
        return userSession.getUser()
                .flatMap(user -> {
                    model.addAttribute("user", user);
                    return postService.findPostById(postId);
                })
                .map(post -> {
                    model.put("post", post);
                    return "post/createOrUpdatePostForm";
                })
                .orElse("redirect:/posts");
    }

    @PostMapping("/{postId}/edit")
    public String processUpdatePostForm(@PathVariable("postId") int postId, @ModelAttribute @Valid Message message, @ModelAttribute UserSession userSession, BindingResult result) {
        Optional.ofNullable(result)
                .filter(r -> !r.hasErrors())
                .flatMap(o -> userSession.getUser())
                .ifPresent(user -> postService.findPostById(postId)
                        .ifPresent(post -> postService.updatePost(message, post)));
        return "redirect:/posts/{postId}";
    }

    @GetMapping("/{postId}/delete")
    public String deletePost(@PathVariable("postId") int postId, @ModelAttribute UserSession userSession) {
        userSession.getUser()
                .ifPresent(user -> postService.findPostById(postId)
                        .ifPresent(post -> postService.deletePost(post, user)));
        return "redirect:/posts";
    }
}
