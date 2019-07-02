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

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class CommentServiceTest extends BaseTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    private User user;
    private Post post;

    @Before
    public void setUp() {
        final String USERNAME = "TestUser";
        final String PASSWORD = "test";
        user = userService.addUser(User.builder().username(USERNAME).password(PASSWORD).build());
        post = postService.addPost(newMessage("Post title 1", "Post body 1"), user).orElse(null);
    }

    @Test
    public void givenUserIsAuthenticated_whenAddComment_thenAppendedToRepository() {
        final Message[] MESSAGES = {
                newMessage("Comment body 0"),
                newMessage("Comment body 1"),
                newMessage("Comment body 2"),
                newMessage("Comment body 3"),
                newMessage("Comment body 4"),
        };

        Arrays.asList(MESSAGES).forEach(message -> post.addComment(newComment(message, post, user)));
        postRepository.save(post); // should call save() unless you invoke Post::addComment from request

        softAssertions.assertThat(post.getComments().size()).isGreaterThan(0);

        IntStream.range(0, MESSAGES.length)
                .forEach(i -> softAssertions.assertThat(post.getComments().get(i).getMessage().getBody())
                        .isEqualTo(MESSAGES[i].getBody()));

        List<Comment> comments = commentService.findAllByPostId(post.getId());

        IntStream.range(0, MESSAGES.length)
                .forEach(i -> softAssertions.assertThat(comments.get(i).getMessage().getBody())
                        .isEqualTo(MESSAGES[i].getBody()));
    }

    @Test
    public void givenUserIsAuthenticated_whenModifyComment_thenCommentIsUpdated() throws Exception {
        final Message MESSAGE1 = newMessage("Comment body 1");
        final Message MESSAGE2 = newMessage("Comment body 2");
        Comment original = commentService.addComment(MESSAGE1, post.getId(), user).orElse(null);
        softAssertions.assertThat(original).isNotNull();

        Comment updated = commentService.updateComment(MESSAGE2, original.getId(), user).orElse(null);
        softAssertions.assertThat(updated).isNotNull();
        softAssertions.assertThat(updated.getMessage().getBody()).isEqualTo(MESSAGE2.getBody());
    }

    @Test
    public void givenUserIsAuthenticated_whenDeleteComment_thenCommentIsDeleted() throws Exception {
        final Message MESSAGE = newMessage("Comment body");
        Comment comment = commentService.addComment(MESSAGE, post.getId(), user).orElse(null);
        softAssertions.assertThat(comment).isNotNull();

        boolean result = commentService.deleteComment(comment.getId(), user);
        softAssertions.assertThat(result).isTrue();

        Comment deleted = commentRepository.findAllByPostIdAndIsDeleted(post.getId(), true).stream()
                .filter(c -> StringUtils.equals(c.getMessage().getBody(), MESSAGE.getBody()))
                .findFirst()
                .orElse(null);
        softAssertions.assertThat(deleted).isNotNull();
    }
}