package ru.yandex.blog.dto.response;

import lombok.Data;
import ru.yandex.blog.domain.Comment;

@Data
public class CommentResponse {
    private Long id;
    private String text;
    private Long postId;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getContent();
        this.postId = comment.getPostId();
    }
}
