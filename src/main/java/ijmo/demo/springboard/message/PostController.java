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
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@Controller
@RequestMapping("/posts")
public class PostController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    private static final String VIEW_POST_CREATE_OR_UPDATE_POST_FORM = "post/createOrUpdatePostForm";
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
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
    public String initCreationForm(@ModelAttribute UserSession userSession) throws Exception {
        userSession.getUser().orElseThrow(UnauthenticatedException::new);
        return "post/createOrUpdatePostForm";
    }

    @PostMapping("/new")
    public String processCreationForm(@ModelAttribute @Valid Message message, @ModelAttribute UserSession userSession, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return VIEW_POST_CREATE_OR_UPDATE_POST_FORM;
        }
        User user = userSession.getUser().orElseThrow(UnauthenticatedException::new);
        return postService.addPost(message, user)
                .map(post -> "redirect:/posts/" + post.getId())
                .orElseThrow(CannotCreateException::new);
    }

    @GetMapping("/{postId}/edit")
    public String initUpdatePostForm(@PathVariable("postId") int postId, @ModelAttribute UserSession userSession, ModelMap model) throws Exception {
        userSession.getUser().orElseThrow(UnauthenticatedException::new);
        postService.findPostById(postId).map(model::addAttribute).orElseThrow(EntityNotFoundException::new);
        return "post/createOrUpdatePostForm";
    }

    @PostMapping("/{postId}/edit")
    public String processUpdatePostForm(@PathVariable("postId") int postId, @ModelAttribute @Valid Message message, @ModelAttribute UserSession userSession, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return VIEW_POST_CREATE_OR_UPDATE_POST_FORM;
        }
        User user = userSession.getUser().orElseThrow(UnauthenticatedException::new);
        Post post = postService.findPostById(postId).orElseThrow(EntityNotFoundException::new);
        int oldPostRevision = post.getMessage().getRevision();
        return postService.updatePost(message, post, user)
                .filter(p -> p.getMessage().getRevision() > oldPostRevision)
                .map(p -> "redirect:/posts/{postId}")
                .orElseThrow(CannotUpdateException::new);
    }

    @PostMapping("/{postId}/delete")
    public String processDeletion(@PathVariable("postId") int postId, @ModelAttribute UserSession userSession) throws Exception {
        User user = userSession.getUser().orElseThrow(UnauthenticatedException::new);
        Post post = postService.findPostById(postId).orElseThrow(EntityNotFoundException::new);
        boolean result = postService.deletePost(post, user);
        if (result) {
            return "redirect:/posts";
        }
        throw new CannotDeleteException();
    }
}
