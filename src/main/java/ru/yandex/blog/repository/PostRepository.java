package ru.yandex.blog.repository;

import ru.yandex.blog.domain.Post;

import java.util.List;

public interface PostRepository {
    List<Post> findPostsWithPagination(String search, int offset, int pageSize);
    Integer countPosts(String search);
    Post create(Post post);
    void delete(Long id);
    Post findById(Long id);
    void update(Post post);
    void updateImagePath(Long postId, String imagePath);
}
