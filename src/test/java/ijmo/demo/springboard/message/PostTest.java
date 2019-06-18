package ijmo.demo.springboard.message;

import ijmo.demo.springboard.BaseTest;
import ijmo.demo.springboard.user.User;
import ijmo.demo.springboard.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PostTest extends BaseTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MessageRepository messageRepository;

    private User user;

    @Before
    public void setUp() {
        user = userRepository.save(User.builder().username("test1").build());
    }

    @Test
    public void addPostTest() {
        postRepository.save(newPost("Post title 1", "Post body 2", user));
        Post found = postRepository.findAll().get(0);
        softAssertions.assertThat(found).isNotNull();
        softAssertions.assertThat(found.getMessage().getTitle())
                .isEqualTo("Post title 1");

        postRepository.save(newPost("Post title 2", "Post body 2", user));

        softAssertions.assertThat(postRepository.findAll().size())
                .isEqualTo(2);

        softAssertions.assertThat(messageRepository.findAll().size())
                .isEqualTo(2);

//        messageRepository.findAll().forEach(message -> softAssertions.assertThat(message.getUser())
//                .isEqualTo(user));
    }
}
