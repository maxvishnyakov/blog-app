package ru.yandex.blog.dto.request;

import lombok.Data;

@Data
public class CommentEditRequest {
    private Long id;
    private String text;
    private Long postId;
}
