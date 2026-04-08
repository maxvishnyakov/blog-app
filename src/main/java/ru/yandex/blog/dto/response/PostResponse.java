package ru.yandex.blog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.blog.domain.Post;

import java.util.List;

@Data
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String title;
    private String text;
    private List<String> tags;
    private Integer likesCount;
    private Integer commentsCount;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.text = post.getTruncatedContent(128);
        this.tags = post.getTags();
        this.likesCount = post.getLikesCount();
        this.commentsCount = post.getCommentsCount();
    }
}
