package ru.yandex.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.blog.domain.Comment;
import ru.yandex.blog.domain.Post;
import ru.yandex.blog.dto.request.CommentRequest;
import ru.yandex.blog.dto.request.PostRequest;
import ru.yandex.blog.dto.response.CommentResponse;
import ru.yandex.blog.dto.response.PagePostResponse;
import ru.yandex.blog.dto.response.PostResponse;
import ru.yandex.blog.service.CommentService;
import ru.yandex.blog.service.PostService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Controller("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    private final String uploadDir = "uploads/posts/";

    @GetMapping
    public ResponseEntity<PagePostResponse> getPosts(@RequestParam String search, @RequestParam Integer pageNumber,
                                                 @RequestParam Integer pageSize) {
        List<Post> posts = postService.getPosts(search, (pageNumber - 1) * pageSize, pageSize);
        int totalCount = postService.getTotalCount(search);
        List<PostResponse> postResponses = posts.stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
        int lastPage = (int) Math.ceil((double) totalCount / pageSize);
        if (lastPage == 0) {
            lastPage = 1;
        }
        boolean hasPrev = pageNumber > 1;
        boolean hasNext = pageNumber < lastPage;
        return ResponseEntity.ok(new PagePostResponse(postResponses, hasPrev, hasNext, lastPage));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        Post post = postService.getPost(id);
        return ResponseEntity.ok(new PostResponse(post));
    }

    @PostMapping
    public ResponseEntity<PostResponse> create(@RequestBody PostRequest postRequest) {
        Post post = postService.create(new Post(postRequest.getTitle(), postRequest.getTags(),
                postRequest.getText()));
        return ResponseEntity.ok(new PostResponse(post));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(@PathVariable Long id, @RequestBody PostRequest postRequest) {
        Post post = postService.updatePost(id, postRequest.getTitle(), postRequest.getText(), postRequest.getTags());
        return ResponseEntity.ok(new PostResponse(post));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<Integer> incrementLikes(@PathVariable Long id) {
        int likesCount = postService.incrementLikesCount(id);
        return ResponseEntity.ok(likesCount);
    }

    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateImage(@PathVariable Long id,
                                            @RequestParam("image") MultipartFile image) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = "post_" + id + "_" + System.currentTimeMillis() + ".jpg";
        Path filePath = uploadPath.resolve(fileName);
        image.transferTo(filePath.toFile());
        postService.updateImage(id, "/" + uploadDir + fileName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws IOException {
        String imagePath = postService.getImagePath(id);
        if (imagePath == null) {
            return ResponseEntity.notFound().build();
        }
        Path path = Paths.get("." + imagePath);
        byte[] imageBytes = Files.readAllBytes(path);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsList(@PathVariable Long id) throws IOException {
        List<Comment> comments = commentService.getCommentsByPostId(id);
        List<CommentResponse> commentsResponses = comments.stream()
                .map(CommentResponse::new)
                .toList();
        return ResponseEntity.ok(commentsResponses);
    }

    @GetMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable Long postId,
                                                      @PathVariable Long commentId) throws IOException {
        Comment comment = commentService.getCommentByIdAndPostId(commentId, postId);
        return ResponseEntity.ok(new CommentResponse(comment));
    }

    @PostMapping("/{postId}/comments/")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long postId,
                                                         @RequestBody CommentRequest commentRequest) throws IOException {
        Comment comment = commentService.create(postId, commentRequest.getText());
        return ResponseEntity.ok(new CommentResponse(comment));
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> editComment(@PathVariable Long postId,
                                                       @PathVariable Long commentId,
                                                       @RequestBody CommentRequest commentRequest) throws IOException {
        Comment comment = new Comment(commentId, postId, commentRequest.getText());
        Comment edittedComment = commentService.edit(comment);
        return ResponseEntity.ok(new CommentResponse(edittedComment));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId,
                                              @PathVariable Long commentId) throws IOException {
        commentService.delete(commentId, postId);
        return ResponseEntity.ok().build();
    }
}
