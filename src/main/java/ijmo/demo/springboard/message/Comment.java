package ijmo.demo.springboard.message;

import ijmo.demo.springboard.model.BaseEntity;
import ijmo.demo.springboard.user.User;
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
@Table(name = "comments")
public class Comment extends BaseEntity {

    @OneToOne(fetch = FetchType.EAGER)
    private Message message;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "comment")
    private List<Message> messages; // history

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Builder
    private Comment(Message message, Post post) {
        message.setComment(this);
        this.message = message;
        this.post = post;
        this.user = message.getUser();
        getMessagesInternal().add(message);
    }

    private List<Message> getMessagesInternal() {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        return messages;
    }
}