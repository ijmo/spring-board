package ijmo.demo.springboard.message;

import ijmo.demo.springboard.handler.BaseController;
import ijmo.demo.springboard.session.UserSession;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
public class CommentController extends BaseController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity showCommentList(@PathVariable("postId") long postId) {
        return ResponseEntity.ok(commentService.findAllByPostId(postId));
    }

    @GetMapping(value={"/posts/{postId}/comments/{commentId}", "/comments/{commentId}"})
    public ResponseEntity showComment(@PathVariable("commentId") long commentId) {
        return commentService.findById(commentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/posts/{postId}/comments/new")
    public ResponseEntity processCreationForm(@PathVariable("postId") long postId, @RequestBody @Valid Message message, @ModelAttribute UserSession userSession, BindingResult result) {
        return Optional.ofNullable(result)
                .filter(r -> !r.hasErrors())
                .flatMap(r -> userSession.getUser())
                .flatMap(user -> commentService.addComment(message, postId))
                .map(comment -> ResponseEntity.ok().build())
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/comments/{commentId}/edit")
    public ResponseEntity processUpdatePostForm(@PathVariable("commentId") long commentId, @RequestBody @Valid Message message, @ModelAttribute UserSession userSession, BindingResult result) {
        return Optional.ofNullable(result)
                .filter(r -> !r.hasErrors())
                .flatMap(r -> userSession.getUser())
                .flatMap(user -> commentService.updateComment(message, commentId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
}