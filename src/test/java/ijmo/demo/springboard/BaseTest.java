package ijmo.demo.springboard;

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

    protected Post newPost(String title, String body, User user) {
        return Post.builder().message(newMessage(title, body, user)).build();
    }


    protected Comment newComment(String body, Post post, User user) {
        return Comment.builder().message(newMessage(body, user)).post(post).build();
    }
}