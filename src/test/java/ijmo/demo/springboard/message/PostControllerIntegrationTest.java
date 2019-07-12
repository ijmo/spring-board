package ijmo.demo.springboard.message;

import ijmo.demo.springboard.test.BaseTest;
import ijmo.demo.springboard.user.User;
import ijmo.demo.springboard.user.UserService;
import org.junit.Assert;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostControllerIntegrationTest extends BaseTest {
    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    private final String USERNAME = "TestUser";
    private final String PASSWORD = "test";

    @Before
    public void setUp() {
        try {
            userService.loadUserByUsername(USERNAME);
        } catch (UsernameNotFoundException e) {
            userService.addUser(User.builder().username(USERNAME).password(PASSWORD).build());
        }
        if (mockMvc == null) {
            mockMvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .apply(SecurityMockMvcConfigurers.springSecurity())
                    .build();
        }
    }

    public UserDetails userPrincipal() {
        return userService.loadUserByUsername(USERNAME);
    }

    @Test
    public void givenUserIsAuthenticated_whenListPosts_thenContainsNewPostButton() throws Exception {
        mockMvc.perform(get("/posts")
                .with(user(userPrincipal()))
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("post/postList"))
                .andExpect(content().string(containsString(USERNAME)))
                .andExpect(content().string(containsString("New Post")));
    }

    @Test
    public void givenUserIsAuthenticated_whenAddPost_thenNewPostShowsInList() throws Exception {
        final Message MESSAGE = newMessage("Post Title", "Post Body");

        MvcResult mvcResult = mockMvc.perform(post("/posts/new")
                .with(user(userPrincipal()))
                .with(csrf())
                .param("title", MESSAGE.getTitle())
                .param("body", MESSAGE.getBody())
                .accept(MediaType.TEXT_HTML))
                .andReturn();

        String redirectUrl = mvcResult.getResponse().getRedirectedUrl();
        Assert.assertNotNull(redirectUrl);

        mockMvc.perform(get(redirectUrl)
                .with(user(userPrincipal()))
                .with(csrf())
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("post/postDetails"))
                .andExpect(content().string(containsString(MESSAGE.getTitle())))
                .andExpect(content().string(containsString(MESSAGE.getBody())));
    }

    @Test
    public void givenUserIsAuthenticated_whenUpdatePost_thenNewPostDetailShows() throws Exception {
        final Message MESSAGE1 = newMessage("Post Title 1", "Post Body 1");
        final Message MESSAGE2 = newMessage("Post Title 2", "Post Body 2");


        MvcResult mvcResult = mockMvc.perform(post("/posts/new")
                .with(user(userPrincipal()))
                .with(csrf())
                .param("title", MESSAGE1.getTitle())
                .param("body", MESSAGE1.getBody())
                .accept(MediaType.TEXT_HTML))
                .andReturn();

        String redirectUrl = mvcResult.getResponse().getRedirectedUrl();
        Assert.assertNotNull(redirectUrl);

        mockMvc.perform(post(redirectUrl + "/edit")
                .with(user(userPrincipal()))
                .with(csrf())
                .param("title", MESSAGE2.getTitle())
                .param("body", MESSAGE2.getBody())
                .accept(MediaType.TEXT_HTML));

        mockMvc.perform(get(redirectUrl)
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("post/postDetails"))
                .andExpect(content().string(containsString(MESSAGE2.getTitle())))
                .andExpect(content().string(containsString(MESSAGE2.getBody())));
    }

    @Test
    public void givenUserIsAuthenticated_whenDeletePost_thenPostIsMissed() throws Exception {
        final Message MESSAGE = newMessage("Post Title", "Post Body");

        MvcResult mvcResult = mockMvc.perform(post("/posts/new")
                .with(user(userPrincipal()))
                .with(csrf())
                .param("title", MESSAGE.getTitle())
                .param("body", MESSAGE.getBody())
                .accept(MediaType.TEXT_HTML))
                .andReturn();

        String redirectUrl = mvcResult.getResponse().getRedirectedUrl();
        Assert.assertNotNull(redirectUrl);

        mockMvc.perform(post(redirectUrl + "/delete")
                .with(user(userPrincipal()))
                .with(csrf())
                .accept(MediaType.TEXT_HTML));

        mockMvc.perform(get(redirectUrl)
                .accept(MediaType.TEXT_HTML))
                .andExpect(content().string(not(containsString(MESSAGE.getTitle()))))
                .andExpect(content().string(not(containsString(MESSAGE.getBody()))))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error"));
    }

    @Test
    public void whenAddPost_thenRedirection() throws Exception {
        final Message MESSAGE = newMessage("Post Title", "Post Body");

        mockMvc.perform(post("/posts/new")
                .param("title", MESSAGE.getTitle())
                .param("body", MESSAGE.getBody())
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void whenUpdatePost_thenRedirection() throws Exception {
        final Message MESSAGE1 = newMessage("Post Title 1", "Post Body 1");
        final Message MESSAGE2 = newMessage("Post Title 2", "Post Body 2");

        MvcResult mvcResult = mockMvc.perform(post("/posts/new")
                .with(user(userPrincipal()))
                .with(csrf())
                .param("title", MESSAGE1.getTitle())
                .param("body", MESSAGE1.getBody())
                .accept(MediaType.TEXT_HTML))
                .andReturn();

        String redirectUrl = mvcResult.getResponse().getRedirectedUrl();
        Assert.assertNotNull(redirectUrl);

        mockMvc.perform(post(redirectUrl + "/edit")
                .param("title", MESSAGE2.getTitle())
                .param("body", MESSAGE2.getBody())
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void whenDeletePost_thenRedirection() throws Exception {
        final Message MESSAGE = newMessage("Post Title", "Post Body");

        MvcResult mvcResult = mockMvc.perform(post("/posts/new")
                .with(user(userPrincipal()))
                .with(csrf())
                .param("title", MESSAGE.getTitle())
                .param("body", MESSAGE.getBody())
                .accept(MediaType.TEXT_HTML))
                .andReturn();

        String redirectUrl = mvcResult.getResponse().getRedirectedUrl();
        Assert.assertNotNull(redirectUrl);

        mockMvc.perform(post(redirectUrl + "/delete")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection());
    }
}