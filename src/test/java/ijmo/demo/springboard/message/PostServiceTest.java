package ijmo.demo.springboard.message;

import ijmo.demo.springboard.test.BaseTest;
import ijmo.demo.springboard.user.User;
import ijmo.demo.springboard.user.UserService;
import org.apache.commons.lang3.StringUtils;
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
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    private User user;

    @Before
    public void setUp() {
        final String USERNAME = "TestUser";
        final String PASSWORD = "test";
        user = userService.addUser(User.builder().username(USERNAME).password(PASSWORD).build());
    }

    @Test
    public void givenUserIsAuthenticated_whenAddPost_thenSavedToRepository() {
        final Message MESSAGE = newMessage("Post title", "Post body");

        postService.addPost(MESSAGE, user);
        Post newPost = postRepository.findAll().stream()
                .filter(p -> StringUtils.equals(p.getMessage().getTitle(), MESSAGE.getTitle())
                        && StringUtils.equals(p.getMessage().getBody(), MESSAGE.getBody()))
                .findFirst()
                .orElse(null);
        softAssertions.assertThat(newPost).isNotNull();
    }

    @Test
    public void givenUserIsAuthenticated_whenUpdatePost_thenSavedToRepository() throws Exception {
        final Message MESSAGE1 = newMessage("Post title 1", "Post body 1");
        final Message MESSAGE2 = newMessage("Post title 2", "Post body 2");

        Post original = newPost(MESSAGE1, user);
        postRepository.save(original);
        postService.updatePost(MESSAGE2, original.getId(), user);

        Post updated = postRepository.findAll().stream()
                .filter(p -> StringUtils.equals(p.getMessage().getTitle(), MESSAGE2.getTitle())
                        && StringUtils.equals(p.getMessage().getBody(), MESSAGE2.getBody()))
                .findFirst()
                .orElse(null);
        softAssertions.assertThat(updated).isNotNull();
    }

    @Test
    public void givenUserIsAuthenticated_whenDeletePost_thenDeletedFlagIsTrue() throws Exception {
        final Message MESSAGE = newMessage("Post title", "Post body");

        Post post = postService.addPost(MESSAGE, user).orElse(null);
        softAssertions.assertThat(post).isNotNull();
        postService.deletePost(post.getId(), user);

        Post found = postRepository.findAll().stream()
                .filter(p -> StringUtils.equals(p.getMessage().getTitle(), MESSAGE.getTitle())
                        && StringUtils.equals(p.getMessage().getBody(), MESSAGE.getBody()))
                .findFirst()
                .orElse(null);
        softAssertions.assertThat(found).isNotNull();
        softAssertions.assertThat(found.getIsDeleted()).isTrue();
    }
}