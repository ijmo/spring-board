package ijmo.demo.springboard.message;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByIsDeletedOrderByCreatedOnDesc(boolean isDeleted);
    Optional<Post> findByIdAndIsDeleted(long postId, boolean isDeleted);
}