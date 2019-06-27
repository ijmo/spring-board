package ijmo.demo.springboard.message;

import ijmo.demo.springboard.UnauthorizedException;
import ijmo.demo.springboard.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private static final Logger log = LoggerFactory.getLogger(PostService.class);
    private PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Cacheable("posts")
    public List<Post> findAll() {
        log.debug("findAll()");
        return postRepository.findAllByIsDeletedOrderByCreatedOnDesc(false);
    }

    public Optional<Post> findPostById(long id) {
        return postRepository.findByIdAndIsDeleted(id, false);
    }

    @CacheEvict(value = "posts", allEntries = true)
    public Optional<Post> addPost(Message message, User user) {
        message.setCreatedOn(ZonedDateTime.now());
        message.setUser(user);
        Post post = Post.builder()
                .message(message)
                .user(message.getUser()).build();
        return Optional.ofNullable(postRepository.save(post));
    }

    @Transactional
    @CacheEvict(value = "posts", allEntries = true)
    public Optional<Post> updatePost(Message message, long postId, User user) throws UnauthorizedException {
        Post post = findPostById(postId).orElseThrow(EntityNotFoundException::new);
        if (!post.isWrittenBy(user)) {
            throw new UnauthorizedException();
        }
        ZonedDateTime now = ZonedDateTime.now();
        message.setRevision(post.getMessages().size() + 1);
        message.setPost(post);
        message.setUser(user);
        message.setCreatedOn(now);
        post.setMessage(message);
        post.setModifiedOn(now);
        post.getMessages().add(message);
        return Optional.ofNullable(postRepository.save(post));
    }

    @CacheEvict(value = "posts", allEntries = true)
    public boolean deletePost(long postId, User user) throws Exception {
        Post post = findPostById(postId).orElseThrow(EntityNotFoundException::new);
        if (!post.isWrittenBy(user)) {
            throw new UnauthorizedException();
        }
        post.setIsDeleted(true);
        post.setModifiedOn(ZonedDateTime.now());
        return postRepository.save(post).getIsDeleted();
    }
}