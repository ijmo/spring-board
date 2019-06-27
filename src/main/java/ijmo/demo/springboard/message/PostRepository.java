package ijmo.demo.springboard.message;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Transactional(readOnly = true)
    @EntityGraph(attributePaths = {"message"})
    List<Post> findAllByIsDeletedOrderByCreatedOnDesc(boolean isDeleted);

    @EntityGraph(attributePaths = {"message"})
    Optional<Post> findByIdAndIsDeleted(long postId, boolean isDeleted);
}