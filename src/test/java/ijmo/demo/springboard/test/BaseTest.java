package ijmo.demo.springboard.test;

import ijmo.demo.springboard.message.Comment;
import ijmo.demo.springboard.message.Message;
import ijmo.demo.springboard.message.Post;
import ijmo.demo.springboard.user.User;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.springframework.test.context.TestPropertySource;

public class BaseTest {
    @Rule
    public JUnitSoftAssertions softAssertions = new JUnitSoftAssertions();

    protected Message newMessage(String body) {
        return Message.builder()
                .body(body).build();
    }

    protected Message newMessage(String title, String body) {
        return Message.builder()
                .title(title)
                .body(body).build();
    }

    protected Post newPost(Message message, User user) {
        return Post.builder().message(message).user(user).build();
    }

    protected Comment newComment(Message message, Post post, User user) {
        return Comment.builder().message(message).user(user).build();
    }
}