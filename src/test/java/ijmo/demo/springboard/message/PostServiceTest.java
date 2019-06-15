package ijmo.demo.springboard.message;


import ijmo.demo.springboard.BaseTest;
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

    @Test
    public void postUpdateTest() {
        final String originalTitle = "Post title 1";
        final String originalBody = "Post body 1";
        final String newTitle = "Post title 2";
        final String newBody = "Post body 2";
        Post original = newPost(originalTitle, originalBody);
        postRepository.save(original);
        postService.updatePost(newMessage(newTitle, newBody), original);

        Post found = postRepository.findAll().get(0);
        softAssertions.assertThat(found.getMessage().getTitle())
                .isEqualTo(newTitle);
        softAssertions.assertThat(found.getMessage().getBody())
                .isEqualTo(newBody);
        softAssertions.assertThat(found.getMessages().size())
                .isEqualTo(2);
    }
}