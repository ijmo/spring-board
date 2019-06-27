package ijmo.demo.springboard.message;

import ijmo.demo.springboard.UnauthorizedException;
import ijmo.demo.springboard.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
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

    private Optional<Comment> findById(long commentId) {
        return commentRepository.findById(commentId);
    }

    public List<Comment> findAllByPostId(long postId) {
        return commentRepository.findAllByPostIdAndIsDeleted(postId, false);
    }

    public Optional<Comment> addComment(Message message, long postId, User user) {
        Post post = postService.findPostById(postId).orElseThrow(EntityNotFoundException::new);
        message.setCreatedOn(ZonedDateTime.now());
        message.setUser(user);
        Comment comment = Comment.builder()
                .message(message)
                .post(post)
                .user(user).build();
        return Optional.ofNullable(commentRepository.save(comment));
    }

    @Transactional
    public Optional<Comment> updateComment(Message message, long commentId, User user) throws Exception {
        Comment comment = commentRepository.findById(commentId).orElseThrow(EntityNotFoundException::new);
        if (!comment.isWrittenBy(user)) {
            throw new UnauthorizedException();
        }
        ZonedDateTime now = ZonedDateTime.now();
        message.setRevision(comment.getMessages().size() + 1);
        message.setComment(comment);
        message.setUser(comment.getUser());
        message.setCreatedOn(now);
        comment.setMessage(message);
        comment.setModifiedOn(now);
        comment.getMessages().add(message);
        return Optional.ofNullable(commentRepository.save(comment));
    }

    public boolean deleteComment(long commentId, User user) throws Exception {
        Comment comment = findById(commentId).orElseThrow(EntityNotFoundException::new);
        if (!comment.isWrittenBy(user)) {
            throw new UnauthorizedException();
        }
        comment.setIsDeleted(true);
        comment.setModifiedOn(ZonedDateTime.now());
        return commentRepository.save(comment).getIsDeleted();
    }
}