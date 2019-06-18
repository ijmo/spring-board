package ijmo.demo.springboard.message;

import ijmo.demo.springboard.session.SessionUtils;
import ijmo.demo.springboard.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("")
    public String showPostList(HttpSession session, Model model, BindingResult result) {
        SessionUtils.getUserFrom(session).ifPresent(user -> model.addAttribute("user", user));
        model.addAttribute("posts", postService.findAllPostsNotDeleted());
        return "post/postList";
    }

    @GetMapping("/{postId}")
    public String showPost(@PathVariable("postId") int postId, HttpSession session, Model model) {
        SessionUtils.getUserFrom(session).ifPresent(user -> model.addAttribute("user", user));
        postService.findPostById(postId).ifPresent(model::addAttribute);
        return "post/postDetails";
    }

    @GetMapping("/new")
    public String initCreationForm(HttpSession session, Model model) {
        return SessionUtils.getUserFrom(session).map(user -> {
            model.addAttribute("user", user);
            return "post/createOrUpdatePostForm";
        }).orElse("redirect:/posts");
    }

    @PostMapping("/new")
    public String processCreationForm(@Valid Message message, HttpSession session, BindingResult result) {
        String path = SessionUtils.getUserFrom(session)
                .map(user -> "/posts/" + postService.addPost(message, user).getId())
                .orElse("/posts");
        return "redirect:" + path;
    }

    @GetMapping("/{postId}/edit")
    public String initUpdatePostForm(@PathVariable("postId") int postId, HttpSession session, ModelMap model) {
        return SessionUtils.getUserFrom(session)
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
    public String processUpdatePostForm(@PathVariable("postId") int postId, @Valid Message message, HttpSession session, BindingResult result) {
        SessionUtils.getUserFrom(session).ifPresent(user ->
                postService.findPostById(postId).ifPresent(post -> postService.updatePost(message, post)));
        return "redirect:/posts/{postId}";
    }

    @GetMapping("/{postId}/delete")
    public String deletePost(@PathVariable("postId") int postId, HttpSession session) {
        SessionUtils.getUserFrom(session).ifPresent(user ->
                postService.findPostById(postId).ifPresent(post -> postService.deletePost(post, user)));
        return "redirect:/posts";
    }
}
