package ijmo.demo.springboard.message;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Transactional
    public Post updatePost(Message newMessage, Post post) {
        newMessage.setRevision(post.getMessages().size() + 1);
        post.setMessage(newMessage);
        post.getMessages().add(newMessage);
        postRepository.save(post);
        return post;
    }
}
