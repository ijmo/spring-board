package ijmo.demo.springboard.message;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
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
                .param("username", USERNAME))
                .andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();
    }

    @Test
    public void whenListComments_thenCommentsAreListed() throws Exception {
        final Message[] COMMENT_MESSAGES = {
                newMessage("Comment Body 1"),
                newMessage("Comment Body 2")
        };

        commentService.addComment(COMMENT_MESSAGES[0], post.getId(), user);
        commentService.addComment(COMMENT_MESSAGES[1], post.getId(), user);

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/" + post.getId() + "/comments")
                .session(session)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(COMMENT_MESSAGES[0].getBody())))
                .andExpect(content().string(containsString(COMMENT_MESSAGES[1].getBody())));
    }

    @Test
    public void givenUserIsAuthenticated_whenAddComments_thenNewCommentIsListed() throws Exception {
        final Message[] COMMENT_MESSAGES = {
                newMessage("Comment Body 1"),
                newMessage("Comment Body 2")
        };

        ObjectMapper mapper = new ObjectMapper();

        for (Message message: COMMENT_MESSAGES) {
            Map<String, String> data = new HashMap<>();
            data.put("body", message.getBody());
            String content = mapper.writeValueAsString(data);
            mockMvc.perform(MockMvcRequestBuilders.post("/posts/" + post.getId() + "/comments/new")
                    .content(content)
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/" + post.getId() + "/comments")
                .session(session)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(COMMENT_MESSAGES[0].getBody())))
                .andExpect(content().string(containsString(COMMENT_MESSAGES[1].getBody())));
    }

    @Test
    public void givenUserIsAuthenticated_whenUpdateComments_thenNewCommentIsShown() throws Exception {
        final Message[] COMMENT_MESSAGES = {
                newMessage("Comment Body 1"),
                newMessage("Comment Body 2")
        };

        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> data = new HashMap<>();
        data.put("body", COMMENT_MESSAGES[0].getBody());
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/" + post.getId() + "/comments/new")
                .content(mapper.writeValueAsString(data))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Get comment's id
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/posts/" + post.getId() + "/comments")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        List<Map<String, Object>> comments = mapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<Map<String, Object>>>(){});

        data.clear();
        data.put("body", COMMENT_MESSAGES[1].getBody());
        mockMvc.perform(MockMvcRequestBuilders.post("/comments/"+ comments.get(0).get("id") + "/edit")
                .content(mapper.writeValueAsString(data))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/" + post.getId() + "/comments")
                .session(session)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(COMMENT_MESSAGES[0].getBody()))))
                .andExpect(content().string(containsString(COMMENT_MESSAGES[1].getBody())));

    }
}
