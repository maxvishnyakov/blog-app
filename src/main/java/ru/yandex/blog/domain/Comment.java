package ru.yandex.blog.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Comment {
    private Long id;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Comment() {

    }

    public Comment(Long id, Long postId, String content) {
        this.id = id;
        this.postId = postId;
        this.content = content;
    }

    public Comment(Long postId, String content) {
        this.postId = postId;
        this.content = content;
    }
}
