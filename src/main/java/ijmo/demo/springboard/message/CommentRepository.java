package ijmo.demo.springboard.message;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"message"})
    List<Comment> findAllByPostIdAndIsDeleted(long postId, boolean isDeleted);
}