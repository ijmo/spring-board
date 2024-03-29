package ijmo.demo.springboard.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ijmo.demo.springboard.model.BaseEntity;
import ijmo.demo.springboard.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.ZonedDateTime;

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

    @Lob
    @Column(name = "body")
    @NotBlank(message = "body is mandatory")
    private String body;

    @Column(name = "created_on")
    private ZonedDateTime createdOn;

    @Column(name = "is_deleted")
    @JsonIgnore
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Post post; // parent

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Comment comment; // parent

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private User user;

    @Builder
    private Message(String title, String body, User user) {
        this.title = title;
        this.body = body;
        this.user = user;
    }
}