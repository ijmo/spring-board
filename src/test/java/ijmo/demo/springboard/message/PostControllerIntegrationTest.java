package ijmo.demo.springboard.message;

import ijmo.demo.springboard.test.BaseTest;
import org.junit.Assert;
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
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerIntegrationTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession session;

    private final String USERNAME = "TestUser";

    public void login() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                .param("username", USERNAME)
                .accept(MediaType.TEXT_HTML))
                .andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();
    }

    @Test
    public void givenUserIsAuthenticated_whenListPosts_thenContainsNewPostButton() throws Exception {
        login();
        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                .session(session)
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(USERNAME)))
                .andExpect(content().string(containsString("btn-new-post")));
    }

    @Test
    public void givenUserIsAuthenticated_whenAddPost_thenNewPostShowsInList() throws Exception {
        final Message MESSAGE = newMessage("Post Title", "Post Body");

        login();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/posts/new")
                .param("title", MESSAGE.getTitle())
                .param("body", MESSAGE.getBody())
                .session(session)
                .accept(MediaType.TEXT_HTML))
                .andReturn();

        String redirectUrl = mvcResult.getResponse().getRedirectedUrl();
        Assert.assertNotNull(redirectUrl);

        mockMvc.perform(MockMvcRequestBuilders.get(redirectUrl)
                .session(session)
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(MESSAGE.getTitle())))
                .andExpect(content().string(containsString(MESSAGE.getBody())));
    }

    @Test
    public void givenUserIsAuthenticated_whenUpdatePost_thenNewPostDetailShows() throws Exception {
        final Message MESSAGE1 = newMessage("Post Title 1", "Post Body 1");
        final Message MESSAGE2 = newMessage("Post Title 2", "Post Body 2");

        login();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/posts/new")
                .param("title", MESSAGE1.getTitle())
                .param("body", MESSAGE1.getBody())
                .session(session)
                .accept(MediaType.TEXT_HTML))
                .andReturn();

        String redirectUrl = mvcResult.getResponse().getRedirectedUrl();
        Assert.assertNotNull(redirectUrl);

        mockMvc.perform(MockMvcRequestBuilders.post(redirectUrl + "/edit")
                .param("title", MESSAGE2.getTitle())
                .param("body", MESSAGE2.getBody())
                .session(session)
                .accept(MediaType.TEXT_HTML));

        mockMvc.perform(MockMvcRequestBuilders.get(redirectUrl)
                .session(session)
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(MESSAGE2.getTitle())))
                .andExpect(content().string(containsString(MESSAGE2.getBody())));
    }

    @Test
    public void givenUserIsAuthenticated_whenDeletePost_thenPostIsMissed() throws Exception {
        final Message MESSAGE = newMessage("Post Title", "Post Body");

        login();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/posts/new")
                .param("title", MESSAGE.getTitle())
                .param("body", MESSAGE.getBody())
                .session(session)
                .accept(MediaType.TEXT_HTML))
                .andReturn();

        String redirectUrl = mvcResult.getResponse().getRedirectedUrl();
        Assert.assertNotNull(redirectUrl);

        mockMvc.perform(MockMvcRequestBuilders.post(redirectUrl + "/delete")
                .session(session)
                .accept(MediaType.TEXT_HTML));

        mockMvc.perform(MockMvcRequestBuilders.get(redirectUrl)
                .session(session)
                .accept(MediaType.TEXT_HTML))
                .andExpect(content().string(not(containsString(MESSAGE.getTitle()))))
                .andExpect(content().string(not(containsString(MESSAGE.getBody()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenAddPost_thenUnauthorized() throws Exception {
        final Message MESSAGE = newMessage("Post Title", "Post Body");

        mockMvc.perform(MockMvcRequestBuilders.post("/posts/new")
                .param("title", MESSAGE.getTitle())
                .param("body", MESSAGE.getBody())
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenUpdatePost_thenUnauthorized() throws Exception {
        final Message MESSAGE1 = newMessage("Post Title 1", "Post Body 1");
        final Message MESSAGE2 = newMessage("Post Title 2", "Post Body 2");

        login();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/posts/new")
                .param("title", MESSAGE1.getTitle())
                .param("body", MESSAGE1.getBody())
                .session(session)
                .accept(MediaType.TEXT_HTML))
                .andReturn();

        String redirectUrl = mvcResult.getResponse().getRedirectedUrl();
        Assert.assertNotNull(redirectUrl);

        mockMvc.perform(MockMvcRequestBuilders.post(redirectUrl + "/edit")
                .param("title", MESSAGE2.getTitle())
                .param("body", MESSAGE2.getBody())
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenDeletePost_thenUnauthorized() throws Exception {
        final Message MESSAGE = newMessage("Post Title", "Post Body");

        login();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/posts/new")
                .param("title", MESSAGE.getTitle())
                .param("body", MESSAGE.getBody())
                .session(session)
                .accept(MediaType.TEXT_HTML))
                .andReturn();

        String redirectUrl = mvcResult.getResponse().getRedirectedUrl();
        Assert.assertNotNull(redirectUrl);

        mockMvc.perform(MockMvcRequestBuilders.post(redirectUrl + "/delete")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isUnauthorized());
    }
}