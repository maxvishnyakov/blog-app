package ru.yandex.blog.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PostAdditionResponse {
    private String title;
    private String text;
    private List<String> tags;
}
