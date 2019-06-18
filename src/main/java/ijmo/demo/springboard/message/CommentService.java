package ijmo.demo.springboard.message;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return commentRepository.findAllByPostId(postId);
    }

    public Optional<Comment> addComment(Message message, long postId) {
        return postService.findPostById(postId).map(post ->
            commentRepository.save(Comment.builder().message(message).post(post).build())
        );
    }

    @Transactional
    public Optional<Comment> updateComment(Message message, long commentId) {
        return commentRepository.findById(commentId)
                .map(comment -> {
                    message.setRevision(comment.getMessages().size() + 1);
                    message.setComment(comment);
                    message.setUser(comment.getUser());
                    comment.setMessage(message);
                    comment.getMessages().add(message);
                    return commentRepository.save(comment);
                });
    }
}
