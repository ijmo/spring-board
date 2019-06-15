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
@Table(name = "comments")
public class Comment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    private Message message;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "comment")
    private List<Message> messages;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Builder
    private Comment(Message message, Post post) {
        message.setComment(this);
        this.message = message;
        this.post = post;
        getMessagesInternal().add(message);
    }

    public List<Message> getMessagesInternal() {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        return messages;
    }
}