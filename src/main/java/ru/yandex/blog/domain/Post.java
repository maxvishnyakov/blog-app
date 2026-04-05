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

    public Post(String title, List<String> tags, String content) {
        this.title = title;
        this.tags = tags;
        this.content = content;
    }

    public Post(Long id, String title, String content, String imagePath, Integer likesCount, Integer commentsCount,
                List<String> tags, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getTruncatedContent(int maxLength) {
        if (content == null) return "";
        if (content.length() <= maxLength) return content;
        return content.substring(0, maxLength) + "...";
    }
}
