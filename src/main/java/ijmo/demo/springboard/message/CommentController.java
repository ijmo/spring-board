package ijmo.demo.springboard.message;

import ijmo.demo.springboard.session.SessionUtils;
import ijmo.demo.springboard.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity showCommentList(@PathVariable("postId") long postId, HttpSession session) {
        return ResponseEntity.ok(commentService.findAllByPostId(postId));
    }

    @GetMapping(value={"/posts/{postId}/comments/{commentId}", "/comments/{commentId}"})
    public ResponseEntity showComment(@PathVariable("commentId") long commentId, HttpSession session) {
        return SessionUtils.getUserFrom(session)
                .flatMap(user -> commentService.findById(commentId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/posts/{postId}/comments/new")
    public ResponseEntity processCreationForm(User user, @PathVariable("postId") long postId, @RequestBody @Valid Message message, BindingResult result) {
        return commentService.addComment(message, postId)
                .map(comment -> ResponseEntity.ok().build())
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/comments/{commentId}/edit")
    public ResponseEntity processUpdatePostForm(User user, @PathVariable("commentId") long commentId, @RequestBody @Valid Message message, BindingResult result) {
        return commentService.updateComment(message, commentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
}