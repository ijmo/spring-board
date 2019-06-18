package ijmo.demo.springboard.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ijmo.demo.springboard.model.BaseEntity;
import ijmo.demo.springboard.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

    @OneToOne(fetch = FetchType.EAGER)
    private Message message;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "comment")
    @JsonIgnore
    private List<Message> messages; // history

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Post post;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "is_deleted")
    @JsonIgnore
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Builder
    private Comment(Message message, Post post, User user) {
        createdAt = ZonedDateTime.now();
        message.setComment(this);
        message.setUser(user);
        this.message = message;
        this.post = post;
        this.user = user;
        getMessagesInternal().add(message);
    }

    private List<Message> getMessagesInternal() {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        return messages;
    }
}