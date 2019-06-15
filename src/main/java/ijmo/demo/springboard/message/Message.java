package ijmo.demo.springboard.message;

import ijmo.demo.springboard.model.BaseEntity;
import ijmo.demo.springboard.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "messages")
public class Message extends BaseEntity {

    @Column(name = "revision")
    private Integer revision = 1;

    @Column(name = "title")
    private String title;

    @Column(name = "body")
    @NotEmpty
    private String body;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @ManyToOne
    private Post post; // parent

    @ManyToOne
    private Comment comment; // parent

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Builder
    private Message(String title, String body, User user) {
        this.title = title;
        this.body = body;
        this.user = user;
    }
}
