package ijmo.demo.springboard.message;

import ijmo.demo.springboard.UnauthorizedException;
import ijmo.demo.springboard.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    public CommentService(CommentRepository commentRepository, PostService postService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
    }

    public Optional<Comment> findById(long commentId) {
        return commentRepository.findById(commentId);
    }

    public List<Comment> findAllByPostId(long postId) {
        return commentRepository.findAllByPostIdAndIsDeleted(postId, false);
    }

    private Optional<Comment> addComment(Message message, long postId) {
        return postService.findPostById(postId).map(post ->
                commentRepository.save(Comment.builder()
                        .message(message)
                        .post(post)
                        .user(message.getUser()).build()));
    }

    public Optional<Comment> addComment(Message message, long postId, User user) {
        message.setUser(user);
        return addComment(message, postId);
    }

    @Transactional
    public Optional<Comment> updateComment(Message message, long commentId, User user) throws Exception {
        Comment comment = commentRepository.findById(commentId).orElseThrow(EntityNotFoundException::new);
        if (!isAuthor(comment, user)) {
            throw new UnauthorizedException();
        }
        message.setRevision(comment.getMessages().size() + 1);
        message.setComment(comment);
        message.setUser(comment.getUser());
        comment.setMessage(message);
        comment.getMessages().add(message);
        return Optional.ofNullable(commentRepository.save(comment));
    }

    public boolean deleteComment(Comment comment, User user) throws UnauthorizedException {
        if (!isAuthor(comment, user)) {
            throw new UnauthorizedException();
        }
        comment.setIsDeleted(true);
        return commentRepository.save(comment).getIsDeleted();
    }

    public boolean isAuthor(Comment comment, User user) {
        return user.getId().equals(comment.getUser().getId());
    }
}
