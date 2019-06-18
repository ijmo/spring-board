package ijmo.demo.springboard.message;

import ijmo.demo.springboard.model.BaseEntity;
import ijmo.demo.springboard.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private List<Message> messages; // history

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "post")
    private List<Comment> comments;

    @Column(name = "comment_count")
    private Integer commentCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Builder
    private Post(Message message, User user) {
        createdAt = LocalDateTime.now();
        message.setPost(this);
        message.setRevision(1);
        message.setUser(user);
        this.message = message;
        this.user = message.getUser();
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
}
