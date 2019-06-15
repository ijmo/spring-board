package ijmo.demo.springboard.message;

import ijmo.demo.springboard.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CommentTest extends BaseTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    public Post newPost(String title, String body) {
        Message message = Message.builder()
                .title(title)
                .body(body).build();
        return Post.builder().message(message).build();
    }

    public Comment newComment(String body, Post post) {
        Message message = Message.builder()
                .body(body).build();
        return Comment.builder().message(message).post(post).build();
    }

    @Before
    public void setUp() {
        postRepository.save(newPost("Post title 1", "Post body"));
    }

    @Test
    public void addCommentTest() {
        Post newPost = postRepository.findAll().get(0);

        IntStream.range(0, 5).forEach( i -> {
            newPost.addComment(newComment("Comment body " + i, newPost));
        });

        Post found = postRepository.findAll().get(0);

        softAssertions.assertThat(found.getComments().get(4).getMessage().getBody())
                .isEqualTo("Comment body 4");
    }
}
