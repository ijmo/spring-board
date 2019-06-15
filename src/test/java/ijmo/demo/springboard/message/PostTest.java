package ijmo.demo.springboard.message;


import ijmo.demo.springboard.BaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PostTest extends BaseTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    public void addPostTest() {
        postRepository.save(newPost("Post title 1", "Post body"));
        Post found = postRepository.findAll().get(0);
        softAssertions.assertThat(found).isNotNull();
        softAssertions.assertThat(found.getMessage().getTitle())
                .isEqualTo("Post title 1");

        found.setCommentCount(100);

        Post secondPost = postRepository.save(newPost("Post title2", "Post body"));
        softAssertions.assertThat(secondPost.getId())
                .isEqualTo(2);
    }
}
