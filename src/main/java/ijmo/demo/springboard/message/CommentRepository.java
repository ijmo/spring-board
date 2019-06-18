package ijmo.demo.springboard.message;

import ijmo.demo.springboard.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost(Post post);
    List<Comment> findAllByPostId(long postId);
    List<Comment> findAllByUser(User user);
}