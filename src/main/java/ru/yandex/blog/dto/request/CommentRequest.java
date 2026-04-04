package ru.yandex.blog.dto.request;

import lombok.Data;

@Data
public class CommentRequest {
    private String text;
    private Long postId;
}
