package ijmo.demo.springboard.message;

import ijmo.demo.springboard.test.BaseTest;
import ijmo.demo.springboard.user.User;
import ijmo.demo.springboard.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;


@RunWith(SpringRunner.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class CommentServiceTest extends BaseTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    private User user;
    private Post post;

    @Before
    public void setUp() {
        user = userRepository.save(User.builder().username("test1").build());
        post = postService.addPost(newMessage("Post title 1", "Post body 1")).orElse(null);
    }

    @Test
    public void givenUserIsAuthenticated_whenAddComment_thenAppendsToRepository() {
        final Message[] MESSAGES = {
                newMessage("Comment body 0"),
                newMessage("Comment body 1"),
                newMessage("Comment body 2"),
                newMessage("Comment body 3"),
                newMessage("Comment body 4"),
        };

        Arrays.asList(MESSAGES).forEach(message -> post.addComment(newComment(message, post, user)));
        postRepository.save(post); // should call save() unless you invoke Post::addComment from request

        softAssertions.assertThat(postService.findPostById(1).isPresent()).isTrue();

        Post post = postService.findPostById(1).get();

        IntStream.range(0, MESSAGES.length)
                .forEach(i -> softAssertions.assertThat(post.getComments().get(i).getMessage().getBody())
                        .isEqualTo(MESSAGES[i].getBody()));

        List<Comment> comments = commentService.findAllByPostId(post.getId());

        IntStream.range(0, MESSAGES.length)
                .forEach(i -> softAssertions.assertThat(comments.get(i).getMessage().getBody())
                        .isEqualTo(MESSAGES[i].getBody()));
    }
}