package ru.yandex.blog.dto.request;

import lombok.Data;

@Data
public class PostEditRequest {
    private Long id;
    private PostRequest postRequest;
}
