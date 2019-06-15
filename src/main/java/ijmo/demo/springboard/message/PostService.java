package ijmo.demo.springboard.message;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {
    private PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public Post addPost(Message message) {
        Post post = Post.builder().message(message).build();
        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(Message message, Post post) {
        message.setRevision(post.getMessages().size() + 1);
        post.setMessage(message);
        post.getMessages().add(message);
        return postRepository.save(post);
    }
}
