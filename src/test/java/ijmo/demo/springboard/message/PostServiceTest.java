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
    public void givenUserIsAuthenticated_whenAddPost_thenSavedToRepository() {
        final Message MESSAGE1 = newMessage("Post title 1", "Post body 1");
        final Message MESSAGE2 = newMessage("Post title 2", "Post body 2");

        postService.addPost(MESSAGE1, user);
        Post found = postRepository.findAll().get(0);

        softAssertions.assertThat(found).isNotNull();
        softAssertions.assertThat(found.getMessage().getTitle())
                .isEqualTo(MESSAGE1.getTitle());

        postService.addPost(MESSAGE2, user);

        softAssertions.assertThat(postRepository.findAll().size())
                .isEqualTo(2);
    }

    @Test
    public void givenUserIsAuthenticated_whenUpdatePost_thenSavedToRepository() throws Exception {
        final Message MESSAGE1 = newMessage("Post title 1", "Post body 1");
        final Message MESSAGE2 = newMessage("Post title 2", "Post body 2");

        Post original = newPost(MESSAGE1, user);
        postRepository.save(original);
        postService.updatePost(MESSAGE2, original.getId(), user);

        Post found = postRepository.findAll().get(0);
        softAssertions.assertThat(found.getMessage().getTitle())
                .isEqualTo(MESSAGE2.getTitle());
        softAssertions.assertThat(found.getMessage().getBody())
                .isEqualTo(MESSAGE2.getBody());
        softAssertions.assertThat(found.getMessages().size())
                .isEqualTo(2);
    }
}