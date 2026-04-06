package ru.yandex.blog.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.blog.configuration.DataSourceConfiguration;
import ru.yandex.blog.domain.Post;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {DataSourceConfiguration.class, H2PostRepository.class})
@TestPropertySource(locations = "classpath:test-application.properties")
public class PostRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private H2PostRepository postRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("ALTER TABLE posts ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("""
                    INSERT INTO posts (title, content, tags, likes_count, comments_count) VALUES
                   (
                       'Введение в Spring Boot',
                       'Spring Boot — это фреймворк для создания микросервисов и веб-приложений на Java. Он упрощает настройку и развертывание приложений. В этой статье мы рассмотрим основные концепции и создадим первое приложение.',
                       '["java", "spring", "tutorial"]',
                       15,
                       2
                   ),
                   (
                       'Работа с JDBC в Spring',
                       'Spring JDBC предоставляет удобные абстракции для работы с базами данных. JdbcTemplate упрощает выполнение SQL-запросов и обработку результатов.',
                       '["java", "spring", "database"]',
                       8,
                       1
                   ),
                   (
                       'REST API с нуля',
                       'REST — это архитектурный стиль для создания веб-сервисов.',
                       '["api", "rest", "web"]',
                       23,
                       1
                   ),
                   (
                       'Еще одна работа с JDBC в Spring',
                       'Spring JDBC предоставляет удобные абстракции для работы с базами данных. JdbcTemplate упрощает выполнение SQL-запросов и обработку результатов.',
                       '["jdbc", "spring", "database"]',
                       5,
                       0
                   )
                """);
    }

    @Test
    public void findPostsWithPagination() {
        List<Post> posts = postRepository.findPostsWithPagination("Spring", 1, 2);

        assertNotNull(posts);
        assertEquals(2, posts.size());
        Post post = posts.getFirst();
        assertEquals(4L, post.getId());
        assertEquals("Еще одна работа с JDBC в Spring", post.getTitle());
    }

    @Test
    public void countPosts() {
        int count = postRepository.countPosts("Spring");

        assertEquals(3, count);
    }

    @Test
    public void create() {
       Post post = new Post("Title", "Content", null, 2, 3,
               Arrays.asList("tag1", "tag2"), LocalDateTime.of(2026, 4, 4, 13, 15),
               LocalDateTime.of(2026, 4, 4, 13, 15));

       Post saved = postRepository.create(post);

        assertNotNull(saved);
        assertEquals(5L, post.getId());
        assertEquals("Title", post.getTitle());
        assertEquals("Content", post.getContent());
        assertEquals(2, post.getLikesCount());
        assertEquals(3, post.getCommentsCount());
        assertEquals(Arrays.asList("tag1", "tag2"), post.getTags());
        assertEquals(LocalDateTime.of(2026, 4, 4, 13, 15), post.getCreatedAt());
        assertEquals(LocalDateTime.of(2026, 4, 4, 13, 15), post.getUpdatedAt());
    }

    @Test
    public void findById() {
        Post post = postRepository.findById(4L);

        assertEquals(4L, post.getId());
    }

    @Test
    public void delete() {
        postRepository.delete(4L);

        int count = postRepository.countPosts("Spring");
        assertEquals(2, count);
    }

    @Test
    public void uploadAndGetImage() {
        byte[] pngStub = new byte[]{(byte) 137, 80, 78, 71};
        postRepository.uploadImage(4L, pngStub);

        byte[] actual = postRepository.findImageById(4L);
        assertEquals(true, Arrays.equals(pngStub, actual));
    }
}
