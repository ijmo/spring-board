package ijmo.demo.springboard.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ijmo.demo.springboard.model.BaseEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "username", unique = true, nullable = false)
    @NotBlank
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "authorities")
    @JsonIgnore
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private String authorities; // Collection<? extends GrantedAuthority>

    public User() {
        this.authorities = "ROLE_USER"; // role should start with "ROLE_" when set manually
    }

    @Builder
    private User(String username, String password, String email) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        return super.hashCode();
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
    }

    public void setAuthorities(String commaSeparatedAuthorities) {
        authorities = commaSeparatedAuthorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = String.join(",", authorities);
    }
}