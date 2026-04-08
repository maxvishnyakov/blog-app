package ru.yandex.blog.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PostRequest {
    private String title;
    private String text;
    private List<String> tags;
}
