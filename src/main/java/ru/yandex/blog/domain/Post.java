package ru.yandex.blog.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class Post {
    private Long id;
    private String title;
    private String content;
    private byte[] image;
    private Integer likesCount;
    private Integer commentsCount;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Post(String title, String content, byte[] image, Integer likesCount, Integer commentsCount,
                List<String> tags, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.title = title;
        this.content = content;
        this.image = image;
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
