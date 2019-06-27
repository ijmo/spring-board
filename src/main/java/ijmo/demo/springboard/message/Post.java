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
@Table(name = "posts")
public class Post extends BaseEntity {

    @OneToOne(fetch = FetchType.EAGER)
    private Message message;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "post")
    @JsonIgnore
    private List<Message> messages; // history

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "post")
    @JsonIgnore
    private List<Comment> comments;

    @Column(name = "comment_count")
    private Integer commentCount = 0;

    @Column(name = "created_on")
    private ZonedDateTime createdOn;

    @Column(name = "modified_on")
    private ZonedDateTime modifiedOn;

    @Column(name = "is_deleted")
    @JsonIgnore
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Builder
    private Post(Message message, User user) {
        message.setPost(this);
        message.setRevision(1);
        message.setUser(user);
        this.message = message;
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

    private List<Comment> getCommentsInternal() {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        return comments;
    }

    public Post addComment(Comment comment) {
        comment.setPost(this);
        getCommentsInternal().add(comment);
        commentCount = comments.size();
        return this;
    }

    public boolean isWrittenBy(User user) {
        return user.getId().equals(getUser().getId());
    }
}