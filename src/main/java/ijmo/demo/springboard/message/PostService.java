package ijmo.demo.springboard.message;

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

    @Transactional
    public Post addPost(Message message, User user) {
        message.setUser(user);
        Post post = Post.builder().message(message).build();
        return postRepository.save(post);
    }

    public List<Post> findAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> findAllPostsNotDeleted() {
        return postRepository.findAllByIsDeleted(false);
    }

    public Optional<Post> findPostById(long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public Post updatePost(Message message, Post post) {
        message.setRevision(post.getMessages().size() + 1);
        message.setPost(post);
        message.setUser(post.getUser());
        post.setMessage(message);
        post.getMessages().add(message);
        return postRepository.save(post);
    }

    public boolean deletePost(Post post, User user) {
        if (user.getId().equals(post.getUser().getId())) {
            post.setIsDeleted(true);
            return postRepository.save(post).getIsDeleted();
        }
        return false;
    }
}
