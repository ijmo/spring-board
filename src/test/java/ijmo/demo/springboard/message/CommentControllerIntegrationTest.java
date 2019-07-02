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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentControllerIntegrationTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    private User testUser;
    private Post aPost;

    private final String USERNAME = "TestUser";
    private final String PASSWORD = "test";

    @Before
    public void setUp() {
        try {
            userService.loadUserByUsername(USERNAME);
        } catch (UsernameNotFoundException e) {
            testUser = userService.addUser(User.builder().username(USERNAME).password(PASSWORD).build());
        }
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .apply(SecurityMockMvcConfigurers.springSecurity())
                    .build();
        }

        if (aPost == null) {
            aPost = postService.addPost(newMessage("Post title", "Post body"), testUser).orElse(null);
        }
    }

    public UserDetails userPrincipal() {
        return userService.loadUserByUsername(USERNAME);
    }

    @Test
    public void whenListComments_thenCommentsAreListed() throws Exception {
        final Message[] MESSAGES = {
                newMessage("Comment Body 1"),
                newMessage("Comment Body 2")
        };

        commentService.addComment(MESSAGES[0], aPost.getId(), testUser);
        commentService.addComment(MESSAGES[1], aPost.getId(), testUser);

        mockMvc.perform(get("/posts/" + aPost.getId() + "/comments")
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

        ObjectMapper mapper = new ObjectMapper();
        for (Message message: MESSAGES) {
            Map<String, String> data = new HashMap<>();
            data.put("body", message.getBody());
            String content = mapper.writeValueAsString(data);
            mockMvc.perform(post("/posts/" + aPost.getId() + "/comments/new")
                    .content(content)
                    .with(user(userPrincipal()))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
        mockMvc.perform(get("/posts/" + aPost.getId() + "/comments")
                .with(user(userPrincipal()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(MESSAGES[0].getBody())))
                .andExpect(content().string(containsString(MESSAGES[1].getBody())));
    }

    @Test
    public void givenUserIsAuthenticated_whenUpdateComments_thenNewCommentIsShown() throws Exception {
        final Message MESSAGE1 = newMessage("Comment Body 1");
        final Message MESSAGE2 = newMessage("Comment Body 2");

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("body", MESSAGE1.getBody());
        mockMvc.perform(post("/posts/" + aPost.getId() + "/comments/new")
                .content(mapper.writeValueAsString(data))
                .with(user(userPrincipal()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Get comment's id
        MvcResult mvcResult = mockMvc.perform(get("/posts/" + aPost.getId() + "/comments")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        List<Map<String, Object>> comments = mapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<Map<String, Object>>>(){});

        data.clear();
        data.put("body", MESSAGE2.getBody());
        mockMvc.perform(post("/comments/"+ comments.get(0).get("id") + "/edit")
                .content(mapper.writeValueAsString(data))
                .with(user(userPrincipal()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/posts/" + aPost.getId() + "/comments")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString(MESSAGE1.getBody()))))
                .andExpect(content().string(containsString(MESSAGE2.getBody())));
    }

    @Test
    public void whenAddComment_thenRedirection() throws Exception {
        final Message MESSAGE = newMessage("Comment Body");

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();

        data.put("body", MESSAGE.getBody());
        mockMvc.perform(post("/posts/" + aPost.getId() + "/comments/new")
                .content(mapper.writeValueAsString(data))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void whenUpdateOrDeleteComment_thenRedirection() throws Exception {
        final Message MESSAGE1 = newMessage("Comment Body 1");
        final Message MESSAGE2 = newMessage("Comment Body 2");

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();

        data.put("body", MESSAGE1.getBody());
        mockMvc.perform(post("/posts/" + aPost.getId() + "/comments/new")
                .content(mapper.writeValueAsString(data))
                .with(user(userPrincipal()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("/posts/" + aPost.getId() + "/comments")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        List<Map<String, Object>> comments = mapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<Map<String, Object>>>(){});

        data.clear();
        data.put("body", MESSAGE2.getBody());

        // Update
        mockMvc.perform(post("/comments/"+ comments.get(0).get("id") + "/edit")
                .content(mapper.writeValueAsString(data))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());

        // Delete
        mockMvc.perform(post("/comments/"+ comments.get(0).get("id") + "/delete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }
}