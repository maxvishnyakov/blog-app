package ru.yandex.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.blog.domain.Post;
import ru.yandex.blog.repository.CommentRepository;
import ru.yandex.blog.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    public List<Post> getPosts(String search, int offset, int pageSize) {
        return postRepository.findPostsWithPagination(search, offset, pageSize);
    }

    public int getTotalCount(String search) {
        return postRepository.countPosts(search);
    }

    public Post create (String title, String text, List<String> tags) {
        Post post = new Post(title, text, null, 0, 0, tags, LocalDateTime.now(),
                LocalDateTime.now());
        return postRepository.create(post);
    }

    public Post updatePost(Long id, String title, String content, List<String> tags) {
        Post post = postRepository.findById(id);
        post.setTitle(title);
        post.setContent(content);
        post.setTags(tags);
        postRepository.update(post);
        return post;
    }

    public void deletePost(Long id) {
        commentRepository.deleteByPostId(id);
        postRepository.delete(id);
    }

    public Post getPost(Long id) {
        return postRepository.findById(id);
    }

    public int incrementLikesCount(Long id) {
        Post post = postRepository.findById(id);
        int likesCount = post.getLikesCount();
        post.setLikesCount(++likesCount);
        postRepository.update(post);
        return post.getLikesCount();
    }

    public void uploadImage(Long id, byte[] imageBytes) {;
        postRepository.uploadImage(id, imageBytes);
    }

    public byte[] getImage(Long id) {
        return postRepository.findImageById(id);
    }
}
