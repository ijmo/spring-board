package ijmo.demo.springboard;


import ijmo.demo.springboard.message.Comment;
import ijmo.demo.springboard.message.Message;
import ijmo.demo.springboard.message.Post;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;

public class BaseTest {
    @Rule
    public JUnitSoftAssertions softAssertions = new JUnitSoftAssertions();

    public Message newMessage(String body) {
        return Message.builder()
                .body(body).build();
    }

    public Message newMessage(String title, String body) {
        return Message.builder()
                .title(title)
                .body(body).build();
    }

    public Post newPost(String title, String body) {
        return Post.builder().message(newMessage(title, body)).build();
    }


    public Comment newComment(String body, Post post) {
        return Comment.builder().message(newMessage(body)).post(post).build();
    }
}
