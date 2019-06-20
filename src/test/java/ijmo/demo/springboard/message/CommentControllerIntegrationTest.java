package ijmo.demo.springboard.message;

import ijmo.demo.springboard.test.BaseTest;
import ijmo.demo.springboard.user.User;
import ijmo.demo.springboard.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerIntegrationTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession session;

    private final String USERNAME = "TestUser";
    private User user;
    private Post post;

    @Before
    public void setUp() throws Exception {
        user = userService.loginOrSignUp(USERNAME);
        post = postService.addPost(newMessage("Post title", "Post body"), user).orElse(null);

//        mockMvc = MockMvcBuilders.standaloneSetup(new CommentController(commentService)).build();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                .param("username", USERNAME)
                .accept(MediaType.TEXT_HTML))
                .andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();
        session.getAttribute("userSession");
    }

    @Test
    public void whenListComments_thenCommentsAreListed() throws Exception {
        final Message COMMENT_MESSAGE1 = newMessage("Comment Body 1");
        final Message COMMENT_MESSAGE2 = newMessage("Comment Body 2");

        commentService.addComment(COMMENT_MESSAGE1, post.getId(), user);
        commentService.addComment(COMMENT_MESSAGE2, post.getId(), user);

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/" + post.getId() + "/comments")
                .session(session)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(COMMENT_MESSAGE1.getBody())))
                .andExpect(content().string(containsString(COMMENT_MESSAGE2.getBody())));
    }
}
