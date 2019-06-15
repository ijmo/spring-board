package ijmo.demo.springboard.message;


import ijmo.demo.springboard.BaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CommentTest extends BaseTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void addCommentTest() {
        String[] commentBodies = {"Comment body 0",
                "Comment body 1",
                "Comment body 2",
                "Comment body 3",
                "Comment body 4",
        };
        Post post = postRepository.save(newPost("Some title", "Blah blah"));

        Arrays.asList(commentBodies).forEach(body -> {
            post.addComment(newComment(body, post));
        });
        postRepository.save(post);

        Post found = postRepository.findAll().get(0);

        IntStream.range(0, commentBodies.length)
                .forEach(i -> softAssertions.assertThat(found.getComments().get(i).getMessage().getBody())
                        .isEqualTo(commentBodies[i]));

        List<Comment> comments = commentRepository.findAllByPost(found);

        IntStream.range(0, commentBodies.length)
                .forEach(i -> softAssertions.assertThat(comments.get(i).getMessage().getBody())
                        .isEqualTo(commentBodies[i]));
    }
}
