package ijmo.demo.springboard.message;

import ijmo.demo.springboard.CannotCreateException;
import ijmo.demo.springboard.CannotDeleteException;
import ijmo.demo.springboard.CannotUpdateException;
import ijmo.demo.springboard.UnauthenticatedException;
import ijmo.demo.springboard.handler.BaseController;
import ijmo.demo.springboard.user.CurrentUser;
import ijmo.demo.springboard.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
public class CommentController extends BaseController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @InitBinder("message")
    public void initMessageBinder(WebDataBinder dataBinder) {
        dataBinder.setValidator(new MessageValidator(false));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity getCommentList(@PathVariable("postId") long postId) {
        return ResponseEntity.ok(commentService.findAllByPostId(postId));
    }

    @PostMapping("/posts/{postId}/comments/new")
    public ResponseEntity processCreation(@PathVariable("postId") long postId, @CurrentUser User user, @Valid @RequestBody Message message, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            throw new BindException(result);
        }
        Optional.ofNullable(user).orElseThrow(UnauthenticatedException::new);
        return commentService.addComment(message, postId, user)
                .map(comment -> ResponseEntity.ok().build())
                .orElseThrow(CannotCreateException::new);
    }

    @PostMapping("/comments/{commentId}/edit")
    public ResponseEntity processUpdate(@PathVariable("commentId") long commentId, @CurrentUser User user, @Valid @RequestBody Message message, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            throw new BindException(result);
        }
        Optional.ofNullable(user).orElseThrow(UnauthenticatedException::new);
        return commentService.updateComment(message, commentId, user)
                .map(ResponseEntity::ok)
                .orElseThrow(CannotUpdateException::new);
    }

    @PostMapping("/comments/{commentId}/delete")
    public ResponseEntity processDeletion(@PathVariable("commentId") long commentId, @CurrentUser User user) throws Exception {
        Optional.ofNullable(user).orElseThrow(UnauthenticatedException::new);
        boolean result = commentService.deleteComment(commentId, user);
        if (result) {
            return ResponseEntity.ok().build();
        }
        throw new CannotDeleteException();
    }
}