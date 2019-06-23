package ijmo.demo.springboard.message;

import ijmo.demo.springboard.CannotCreateException;
import ijmo.demo.springboard.CannotDeleteException;
import ijmo.demo.springboard.CannotUpdateException;
import ijmo.demo.springboard.UnauthenticatedException;
import ijmo.demo.springboard.handler.BaseController;
import ijmo.demo.springboard.session.UserSession;
import ijmo.demo.springboard.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

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

//    @GetMapping(value={"/posts/{postId}/comments/{commentId}", "/comments/{commentId}"})
//    public ResponseEntity showComment(@PathVariable("commentId") long commentId) {
//        return commentService.findById(commentId)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

    @PostMapping("/posts/{postId}/comments/new")
    public ResponseEntity processCreationForm(@PathVariable("postId") long postId, @RequestBody @Valid Message message, @ModelAttribute UserSession userSession, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            throw new CannotCreateException();
        }
        User user = userSession.getUser().orElseThrow(UnauthenticatedException::new);
        return commentService.addComment(message, postId, user)
                .map(comment -> ResponseEntity.ok().build())
                .orElseThrow(CannotCreateException::new);
    }

    @PostMapping("/comments/{commentId}/edit")
    public ResponseEntity processUpdateCommentForm(@PathVariable("commentId") long commentId, @RequestBody @Valid Message message, @ModelAttribute UserSession userSession, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            throw new CannotCreateException();
        }
        User user = userSession.getUser().orElseThrow(UnauthenticatedException::new);
        return commentService.updateComment(message, commentId, user)
                .map(ResponseEntity::ok)
                .orElseThrow(CannotUpdateException::new);
    }

    @PostMapping("/comments/{commentId}/delete")
    public ResponseEntity processDeletion(@PathVariable("commentId") long commentId, @ModelAttribute UserSession userSession) throws Exception {
        User user = userSession.getUser().orElseThrow(UnauthenticatedException::new);
        Comment comment = commentService.findById(commentId).orElseThrow(EntityNotFoundException::new);
        boolean result = commentService.deleteComment(comment, user);
        if (result) {
            return ResponseEntity.ok().build();
        }
        throw new CannotDeleteException();
    }
}