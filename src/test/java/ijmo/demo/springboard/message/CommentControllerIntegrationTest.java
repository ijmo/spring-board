package ijmo.demo.springboard.message;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ijmo.demo.springboard.test.BaseTest;
import ijmo.demo.springboard.user.User;
import ijmo.demo.springboard.user.UserService;
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

    public void login() throws Exception {
        user = userService.loginOrSignUp(USERNAME);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                .param("username", USERNAME))
                .andReturn();
        session = (MockHttpSession) mvcResult.getRequest().getSession();
    }

    public void add1Post() {
        post = postService.addPost(newMessage("Post title", "Post body"), user).orElse(null);
    }

    @Test
    public void whenListComments_thenCommentsAreListed() throws Exception {
        final Message[] MESSAGES = {
                newMessage("Comment Body 1"),
                newMessage("Comment Body 2")
        };

        login();
        add1Post();

        commentService.addComment(MESSAGES[0], post.getId(), user);
        commentService.addComment(MESSAGES[1], post.getId(), user);

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/" + post.getId() + "/comments")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(MESSAGES[0].getBody())))
                .andExpect(content().string(containsString(MESSAGES[1].getBody())));
    }

    @Test
    public void givenUserIsAuthenticated_whenAddComments_thenNewCommentIsListed() throws Exception {
        final Message[] MESSAGES = {
                newMessage("Comment Body 1"),
                newMessage("Comment Body 2")
        };
        login();
        add1Post();

        ObjectMapper mapper = new ObjectMapper();
        for (Message message: MESSAGES) {
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
                .andExpect(content().string(containsString(MESSAGES[0].getBody())))
                .andExpect(content().string(containsString(MESSAGES[1].getBody())));
    }

    @Test
    public void givenUserIsAuthenticated_whenUpdateComments_thenNewCommentIsShown() throws Exception {
        final Message MESSAGE1 = newMessage("Comment Body 1");
        final Message MESSAGE2 = newMessage("Comment Body 2");

        login();
        add1Post();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("body", MESSAGE1.getBody());
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
        data.put("body", MESSAGE2.getBody());
        mockMvc.perform(MockMvcRequestBuilders.post("/comments/"+ comments.get(0).get("id") + "/edit")
                .content(mapper.writeValueAsString(data))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/" + post.getId() + "/comments")
                .session(session)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(MESSAGE1.getBody()))))
                .andExpect(content().string(containsString(MESSAGE2.getBody())));
    }

    @Test
    public void whenAddComment_thenUnauthorized() throws Exception {
        final Message MESSAGE = newMessage("Comment Body");

        login();
        add1Post();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();

        data.put("body", MESSAGE.getBody());
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/" + post.getId() + "/comments/new")
                .content(mapper.writeValueAsString(data))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenUpdateOrDeleteComment_thenUnauthorized() throws Exception {
        final Message MESSAGE1 = newMessage("Comment Body 1");
        final Message MESSAGE2 = newMessage("Comment Body 2");

        login();
        add1Post();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();

        data.put("body", MESSAGE1.getBody());
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/" + post.getId() + "/comments/new")
                .content(mapper.writeValueAsString(data))
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/posts/" + post.getId() + "/comments")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        List<Map<String, Object>> comments = mapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<Map<String, Object>>>(){});

        data.clear();
        data.put("body", MESSAGE2.getBody());

        // Update
        mockMvc.perform(MockMvcRequestBuilders.post("/comments/"+ comments.get(0).get("id") + "/edit")
                .content(mapper.writeValueAsString(data))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        // Delete
        mockMvc.perform(MockMvcRequestBuilders.post("/comments/"+ comments.get(0).get("id") + "/delete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
