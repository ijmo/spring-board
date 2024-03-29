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

    @Column(name = "created_on")
    private ZonedDateTime createdOn;

    @Column(name = "modified_on")
    private ZonedDateTime modifiedOn;

    @Column(name = "is_deleted")
    @JsonIgnore
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Builder
    private Comment(Message message, Post post, User user) {
        message.setComment(this);
        message.setUser(user);
        this.message = message;
        this.post = post;
        this.user = user;
        this.createdOn = message.getCreatedOn();
        getMessagesInternal().add(message);
    }

    private List<Message> getMessagesInternal() {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        return messages;
    }

    public boolean isWrittenBy(User user) {
        return user.getId().equals(getUser().getId());
    }
}