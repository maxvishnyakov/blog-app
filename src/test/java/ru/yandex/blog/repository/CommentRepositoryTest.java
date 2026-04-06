package ru.yandex.blog.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.blog.configuration.DataSourceConfiguration;
import ru.yandex.blog.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, H2CommentRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class CommentRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private H2CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("ALTER TABLE comments ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("""
                    INSERT INTO comments (post_id, content) VALUES
                       (1, 'Отличная статья! Очень помогла разобраться.'),
                       (1, 'А где можно найти примеры кода?'),
                       (2, 'JdbcTemplate действительно удобная штука.'),
                       (3, 'Спасибо за материал! Жду продолжения.')
                """);
    }

    @Test
    public void create() {
        Comment comment = new Comment(1L, "Content");

        Comment saved = commentRepository.create(comment);

        assertNotNull(saved);
        assertEquals(5L, comment.getId());
        assertEquals(1L, comment.getPostId());
        assertEquals("Content", comment.getContent());
    }

    @Test
    public void findByPostId() {
        List<Comment> comments = commentRepository.findByPostId(1L);

        assertNotNull(comments);
        assertEquals(2, comments.size());
        Comment first = comments.getFirst();
        assertEquals(1L, first.getId());
        assertEquals(1L, first.getPostId());
        assertEquals("Отличная статья! Очень помогла разобраться.", first.getContent());
    }

    @Test
    public void findByIdAndPostId() {
        Comment comment = commentRepository.findByIdAndPostId(2L, 1L);

        assertNotNull(comment);
        assertEquals(2L, comment.getId());
        assertEquals(1L, comment.getPostId());
        assertEquals("А где можно найти примеры кода?", comment.getContent());
    }

    @Test
    public void update() {
        Comment comment = new Comment(2L, 1L, "Content", LocalDateTime.of(2026, 4, 4, 13, 15),
                LocalDateTime.of(2026, 4, 4, 13, 15));
        commentRepository.update(comment);

        Comment updatedComment = commentRepository.findByIdAndPostId(2L, 1L);
        assertNotNull(comment);
        assertEquals(2L, updatedComment.getId());
        assertEquals(1L, updatedComment.getPostId());
        assertEquals("Content", updatedComment.getContent());
    }

    @Test
    public void deleteByPostId() {
        commentRepository.deleteByPostId(1L);

        List<Comment> comments = commentRepository.findByPostId(1L);

        assertEquals(true, comments.isEmpty());
    }

    @Test
    public void deleteByIdAndPostId() {
        commentRepository.deleteByIdAndPostId(2L, 1L);

        assertThrows(EmptyResultDataAccessException.class, () -> {
            commentRepository.findByIdAndPostId(2L, 1L);
        });
    }
}
