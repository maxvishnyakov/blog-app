package ru.yandex.blog.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Post {
    private Long id;
    private String title;
    private String content;
    private String imagePath;
    private Integer likesCount;
    private Integer commentsCount;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
