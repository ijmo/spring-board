package ijmo.demo.springboard.system;

import ijmo.demo.springboard.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private Environment env;
    private UserService userService;

    public SecurityConfig(Environment env, UserService userService) {
        this.env = env;
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final List<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        http
                .authorizeRequests()
                .antMatchers("/posts/new",
                        "/posts/{\\d+}/edit",
                        "/posts/{\\d+}/delete",
                        "/posts/{\\d+}/comments/new",
                        "/comments/{\\d+}/edit",
                        "/comments/{\\d+}/delete").hasRole("USER")
                .antMatchers("/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID");

        if (activeProfiles.contains("dev")) {
            http.csrf().requireCsrfProtectionMatcher(new AntPathRequestMatcher("!/h2-console/**")) ;
            http.headers().frameOptions().disable(); // for h2-console
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(userService.passwordEncoder());
        return authProvider;
    }
}