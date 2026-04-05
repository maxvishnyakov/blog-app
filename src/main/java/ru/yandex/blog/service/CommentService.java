package ru.yandex.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.blog.domain.Comment;
import ru.yandex.blog.domain.Post;
import ru.yandex.blog.repository.CommentRepository;
import ru.yandex.blog.repository.PostRepository;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostRepository postRepository;

    public Comment getCommentByIdAndPostId(long id, long postId) {
        return commentRepository.findByIdAndPostId(id, postId);
    }

    public Comment create(long postId, String content) {
        Comment comment = new Comment(postId, content);
        Comment newComment = commentRepository.create(comment);
        Post post = postRepository.findById(postId);
        int commentsCount = post.getCommentsCount();
        post.setCommentsCount(++commentsCount);
        postRepository.update(post);
        return newComment;
    }

    public Comment edit(Comment comment) {
        commentRepository.update(comment);
        return comment;
    }

    public void delete(long id, long postId) {
        commentRepository.deleteByIdAndPostId(id, postId);
    }

    public List<Comment> getCommentsByPostId(long postId) {
        return commentRepository.findByPostId(postId);
    }
}
