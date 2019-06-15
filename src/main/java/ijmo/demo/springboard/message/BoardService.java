package ijmo.demo.springboard.message;

import org.springframework.stereotype.Service;

@Service
public class BoardService {
    private MessageRepository messageRepository;
    private PostRepository postRepository;
    private CommentRepository commentRepository;

    public Post addPost(Post post) {
        return postRepository.save(post);
    }
}
