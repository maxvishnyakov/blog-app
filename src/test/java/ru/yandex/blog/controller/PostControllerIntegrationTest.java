package ru.yandex.blog.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.blog.configuration.DataSourceConfiguration;
import ru.yandex.blog.configuration.WebConfiguration;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(classes = {
        DataSourceConfiguration.class,
        WebConfiguration.class
})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
public class PostControllerIntegrationTest {

    private static final String EXPECTED_FILES_DIR = "expected/";
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        initializeData();
    }

    @Test
    void getPosts() throws Exception {
        String expected = readJsonFile(EXPECTED_FILES_DIR + "posts-list.json");

        mockMvc.perform(get("/api/posts")
                        .param("search", "spring")
                        .param("pageNumber", "1")
                        .param("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(expected))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getPost() throws Exception {
        String expected = readJsonFile(EXPECTED_FILES_DIR + "get-post.json");

        mockMvc.perform(get("/api/posts/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(expected))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void createPost() throws Exception {
        String json = """ 
                    {"title": "Название поста 3", "text": "Текст поста в формате Markdown...","tags": ["tag_1", "tag_2"]}
                """;
        String expected = readJsonFile(EXPECTED_FILES_DIR + "create-post.json");

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expected));
    }

    @Test
    void editPost() throws Exception {
        String json = """ 
                    {"title": "Отредактированный пост", "text": "Отредактированный текст поста", "tags": ["edited_tag_1", "tag_2"]}
                """;
        String expected = readJsonFile(EXPECTED_FILES_DIR + "edit-post.json");

        mockMvc.perform(put("/api/posts/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expected));
    }

    @Test
    void deletePost() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/api/posts/{id}", id))
                .andExpect(status().isOk());

        List<Long> ids = jdbcTemplate.queryForList("SELECT id FROM posts WHERE id = ?", Long.class, id);
        assertThat(ids).isEmpty();
    }

    @Test
    void incrementLikes() throws Exception {
        Long id = 1L;

        mockMvc.perform(post("/api/posts/{id}/likes", id))
                .andExpect(status().isOk());

        List<Long> likes = jdbcTemplate.queryForList("SELECT likes_count FROM posts WHERE id = ?", Long.class, id);
        assertThat(likes.getFirst()).isEqualTo(16);
    }

    @Test
    void uploadAndGetImage() throws Exception {
        byte[] pngStub = new byte[]{(byte) 137, 80, 78, 71};
        MockMultipartFile file = new MockMultipartFile("image", "image.png", "image/png", pngStub);

        MockMultipartHttpServletRequestBuilder builder =
                (MockMultipartHttpServletRequestBuilder) multipart("/api/posts/{id}/image", 1L)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        });

        mockMvc.perform(builder)
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/{id}/image", 1L))
                .andExpect(status().isOk())
                .andExpect(content().bytes(pngStub));
    }

    @Test
    void getComments() throws Exception {
        String expected = readJsonFile(EXPECTED_FILES_DIR + "get-comments.json");

        mockMvc.perform(get("/api/posts/{id}/comments", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(expected))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getComment() throws Exception {
        String expected = readJsonFile(EXPECTED_FILES_DIR + "get-comment.json");

        mockMvc.perform(get("/api/posts/{postId}/comments/{commentId}", 1L, 2L))
                .andExpect(status().isOk())
                .andExpect(content().json(expected))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createComment() throws Exception {
        String json = """ 
                    {"text": "Крутой пост !", "postId": "2"}
                """;
        String expected = readJsonFile(EXPECTED_FILES_DIR + "create-comment.json");

        mockMvc.perform(post("/api/posts/{postId}/comments", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expected));
    }

    @Test
    void editComment() throws Exception {
        String json = """ 
                    {"text": "Отредактированный комментарий", "postId": "1"}
                """;
        String expected = readJsonFile(EXPECTED_FILES_DIR + "edit-comment.json");

        mockMvc.perform(put("/api/posts/{postId}/comments/{2}", 1, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expected));
    }

    @Test
    void deleteComment() throws Exception {
        Long postId = 1L;
        Long commentId = 2L;

        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", postId, commentId))
                .andExpect(status().isOk());

        List<Long> ids = jdbcTemplate.queryForList("SELECT id FROM comments WHERE id = ? AND post_id = ?",
                Long.class, commentId, postId);
        assertThat(ids).isEmpty();
    }

    private String readJsonFile(String path) throws Exception {
        return new ClassPathResource(path)
                .getContentAsString(StandardCharsets.UTF_8);
    }

    private void initializeData() {
        jdbcTemplate.execute("ALTER TABLE posts ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("ALTER TABLE comments ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("DELETE FROM comments");
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
                       'REST — это архитектурный стиль для создания веб-сервисов. В этом посте разберём основные принципы: ресурсы, HTTP методы, статус коды и форматы данных.',
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

        jdbcTemplate.execute("""
                    INSERT INTO comments (post_id, content) VALUES
                       (1, 'Отличная статья! Очень помогла разобраться.'),
                       (1, 'А где можно найти примеры кода?'),
                       (2, 'JdbcTemplate действительно удобная штука.'),
                       (3, 'Спасибо за материал! Жду продолжения.')
                """);
    }
}
