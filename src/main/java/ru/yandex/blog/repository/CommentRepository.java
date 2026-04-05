package ru.yandex.blog.repository;

import ru.yandex.blog.domain.Comment;

import java.util.List;

public interface CommentRepository {
    Comment create(Comment comment);
    List<Comment> findByPostId(long postId);
    Comment findByIdAndPostId(long id, long postId);
    void update(Comment comment);
    void deleteByIdAndPostId(long id, long postId);
    void deleteByPostId(Long postId);
}
