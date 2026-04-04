package ru.yandex.blog.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PostResponse {
    private Long id;
    private String title;
    private String text;
    private List<String> tags;
    private Integer likesCount;
    private Integer commentsCount;
}
