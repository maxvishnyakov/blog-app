package ru.yandex.blog.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PagePostResponse {
    private List<PostResponse> posts;
    private Boolean hasPrev;
    private Boolean hasNext;
    private Integer lastPage;
}
