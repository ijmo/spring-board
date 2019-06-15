package ijmo.demo.springboard.message;

import ijmo.demo.springboard.BaseTest;
import ijmo.demo.springboard.user.User;
import ijmo.demo.springboard.user.UserRepository;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;


public class CommentServiceTest extends BaseTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User user;

    @Before
    public void setUp() {
        user = userRepository.save(User.builder().username("test1").build());
    }


}