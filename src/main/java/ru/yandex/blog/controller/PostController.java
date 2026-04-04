package ru.yandex.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("/api")
public class PostController {

    @GetMapping
    public PostsResponse posts(String search, int pageNumber, int pageSize) {

    }
}
