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

    public Post newPost(String title, String body) {
        Message message = Message.builder()
                .title(title)
                .body(body).build();
        return Post.builder().message(message).build();
    }

    @Test
    public void addPostTest() {
        Post newPost = newPost("Post title 1", "Post body");
        Post found = postRepository.save(newPost);
        System.out.println("found: " + found.getMessage().getId());
        softAssertions.assertThat(newPost.getMessage().getTitle())
                .isEqualTo(found.getMessage().getTitle());

        Post secondPost = postRepository.save(newPost("Post title2", "Post body"));
        softAssertions.assertThat(secondPost.getId())
                .isEqualTo(2);
    }

    @Test
    public void updatePostTest() {
        Post post = postRepository.save(newPost("Post title1", "Post body"));
        Post updated = post.update(Message.builder().title("Post title2").body("Post body").build());
        softAssertions.assertThat(updated.getMessage().getTitle())
                .isEqualTo("Post title2");
        softAssertions.assertThat(updated.getMessage().getRevision())
                .isEqualTo(2);
    }
}
