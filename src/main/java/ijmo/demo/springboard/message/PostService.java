package ijmo.demo.springboard.message;

import ijmo.demo.springboard.UnauthorizedException;
import ijmo.demo.springboard.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Optional<Post> addPost(Message message) {
        if (message.getUser() != null) {
            Post post = Post.builder().message(message).user(message.getUser()).build();
            return Optional.ofNullable(postRepository.save(post));
        }
        return Optional.empty();
    }

    public Optional<Post> addPost(Message message, User user) {
        message.setUser(user);
        return addPost(message);
    }

    public List<Post> findAll() {
        return postRepository.findAllByIsDeletedOrderByCreatedOnDesc(false);
    }

    public Optional<Post> findPostById(long id) {
        return postRepository.findByIdAndIsDeleted(id, false);
    }

    @Transactional
    public Optional<Post> updatePost(Message message, Post post, User user) throws UnauthorizedException {
        if (!isAuthor(post, user)) {
            throw new UnauthorizedException();
        }
        message.setRevision(post.getMessages().size() + 1);
        message.setPost(post);
        message.setUser(post.getUser());
        post.setMessage(message);
        post.getMessages().add(message);
        return Optional.ofNullable(postRepository.save(post));
    }

    public boolean deletePost(Post post, User user) throws UnauthorizedException {
        if (!isAuthor(post, user)) {
            throw new UnauthorizedException();
        }
        post.setIsDeleted(true);
        return postRepository.save(post).getIsDeleted();
    }

    public boolean isAuthor(Post post, User user) {
        return user.getId().equals(post.getUser().getId());
    }
}
