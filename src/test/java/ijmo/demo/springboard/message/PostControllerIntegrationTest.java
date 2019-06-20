package ijmo.demo.springboard.message;


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
public class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession session;

    private final String USERNAME = "TestUser";

    @Before
    public void setUp() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/login")
                .param("username", USERNAME)
                .accept(MediaType.TEXT_HTML))
                .andReturn();

        session = (MockHttpSession) mvcResult.getRequest().getSession();
    }

    @Test
    public void givenUserIsAuthenticated_whenListPosts_thenContainsNewPostButton() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                .session(session)
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(USERNAME)))
                .andExpect(content().string(containsString("btn-new-post")));
    }
}
