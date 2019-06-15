package ijmo.demo.springboard.message;

import ijmo.demo.springboard.BaseTest;
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


@RunWith(SpringRunner.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class PostServiceTest extends BaseTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    private User user;

    @Before
    public void setUp() {
        user = userRepository.save(User.builder().username("test1").build());
    }

    @Test
    public void postAddTest() {
        // TODO
    }

    @Test
    public void postUpdateTest() {
        final String originalTitle = "Post title 1";
        final String originalBody = "Post body 1";
        final String newTitle = "Post title 2";
        final String newBody = "Post body 2";
        Post original = newPost(originalTitle, originalBody, user);
        postRepository.save(original);
        postService.updatePost(newMessage(newTitle, newBody, user), original);

        Post found = postRepository.findAll().get(0);
        softAssertions.assertThat(found.getMessage().getTitle())
                .isEqualTo(newTitle);
        softAssertions.assertThat(found.getMessage().getBody())
                .isEqualTo(newBody);
        softAssertions.assertThat(found.getMessages().size())
                .isEqualTo(2);
    }
}