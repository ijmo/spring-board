package ijmo.demo.springboard.test;

import ijmo.demo.springboard.message.Comment;
import ijmo.demo.springboard.message.Message;
import ijmo.demo.springboard.message.Post;
import ijmo.demo.springboard.user.User;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;

public class BaseTest {
    @Rule
    public JUnitSoftAssertions softAssertions = new JUnitSoftAssertions();

    protected Message newMessage(String body, User user) {
        return Message.builder()
                .body(body)
                .user(user).build();
    }

    protected Message newMessage(String title, String body, User user) {
        return Message.builder()
                .title(title)
                .body(body)
                .user(user).build();
    }

    protected Post newPost(Message message, User user) {
        return Post.builder().message(message).user(user).build();
    }

    protected Comment newComment(Message message, User user) {
        return Comment.builder().message(message).user(user).build();
    }
}