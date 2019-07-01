package ijmo.demo.springboard.user;

import ijmo.demo.springboard.test.BaseTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class UserServiceTest extends BaseTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    public void whenAddUser_thenSavedToRepository() {
        final String USERNAME = "TestUser";
        final String PASSWORD = "test";

        userService.addUser(User.builder().username(USERNAME).password(PASSWORD).build());
        Optional<User> optionalUser = userRepository.findByUsername(USERNAME);

        softAssertions.assertThat(optionalUser.orElse(null)).isNotNull();

        softAssertions.assertThat(optionalUser.get().getUsername())
                .isEqualTo(USERNAME);
    }
}
