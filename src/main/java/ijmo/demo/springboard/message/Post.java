package ijmo.demo.springboard.message;


import ijmo.demo.springboard.model.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post extends BaseEntity {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Message message;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "post")
    private List<Message> messages;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "post")
    private List<Comment> comments;

    @Column(name = "comment_count")
    private Integer commentCount = 0;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Builder
    private Post(Message message) {
        message.setPost(this);
        message.setRevision(1);
        this.message = message;
        getMessagesInternal().add(message);
    }

    public List<Message> getMessagesInternal() {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        return messages;
    }

    public List<Comment> getCommentsInternal() {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        return comments;
    }

    public Post addComment(Comment comment) {
        comment.setPost(this);
        getCommentsInternal().add(comment);
        commentCount++;
        return this;
    }
}