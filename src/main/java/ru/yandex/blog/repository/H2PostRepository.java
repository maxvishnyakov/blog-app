package ru.yandex.blog.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.blog.domain.Post;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class H2PostRepository implements PostRepository{
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RowMapper<Post> postRowMapper = (resultSet, row) -> {
        Long id = resultSet.getLong("id");
        String title = resultSet.getString("title");
        String content = resultSet.getString("content");
        byte[] image = resultSet.getBytes("image");
        Integer likesCount = resultSet.getInt("likes_count");
        Integer commentsCount = resultSet.getInt("comments_count");
        List<String> tags = convertJsonToTags(resultSet.getString("tags"));
        LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updatedAt = resultSet.getTimestamp("updated_at").toLocalDateTime();
        return new Post(id, title, content, image, likesCount, commentsCount,
                tags, createdAt, updatedAt);
    };

    public H2PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Post> findPostsWithPagination(String search, int offset, int pageSize) {
        String sql = """
                SELECT * FROM posts WHERE
                LOWER(title) LIKE LOWER(?) OR
                LOWER(content) LIKE LOWER(?)
                ORDER BY created_at DESC, id DESC LIMIT ? OFFSET ?
                """;
        String searchPattern = "%" + search + "%";
        return jdbcTemplate.query(sql, postRowMapper,
                searchPattern, searchPattern, pageSize, offset);
    }

    @Override
    public Integer countPosts(String search) {
        String sql = """
                SELECT COUNT(*) FROM posts WHERE
                LOWER(title) LIKE LOWER(?) OR
                LOWER(content) LIKE LOWER(?)
                """;
        String searchPattern = "%" + search + "%";
        return jdbcTemplate.queryForObject(sql, Integer.class, searchPattern, searchPattern);
    }

    @Override
    public Post create(Post post) {
        String sql = """
                INSERT INTO posts (title, content, image, likes_count,
                comments_count, tags, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            putPostData(statement, post);
            statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            return statement;
        }, keyHolder);
        Map<String, Object> keys = keyHolder.getKeys();
        post.setId(((Number) keys.get("ID")).longValue());
        return post;
    }

    @Override
    public Post findById(Long id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, postRowMapper, id);
    }

    @Override
    public void update(Post post) {
        String sql = """
                UPDATE posts SET title = ?, content = ?, tags = ?,
                likes_count = ?, comments_count = ?, image = ?,
                updated_at = ? WHERE id = ?
                """;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            putPostData(ps, post);
            ps.setLong(8, post.getId());
            return ps;
        });
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void uploadImage(Long postId, byte[] imageBytes) {
        String sql = "UPDATE posts SET image = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, imageBytes, Timestamp.valueOf(LocalDateTime.now()), postId);
    }

    @Override
    public byte[] findImageById(Long id) {
        String sql = "SELECT image FROM posts WHERE id=?";
        return jdbcTemplate.query(
                sql, preparedStatement -> preparedStatement.setLong(1, id),
                resultSet -> resultSet.next() ? resultSet.getBytes("image") : null
        );
    }

    private void putPostData(PreparedStatement statement, Post post) throws SQLException {
        statement.setString(1, post.getTitle());
        statement.setString(2, post.getContent());
        statement.setBytes(3, post.getImage());
        statement.setInt(4, post.getLikesCount());
        statement.setInt(5, post.getCommentsCount());
        statement.setString(6, convertTagsToJson(post.getTags()));
        statement.setTimestamp(7, Timestamp.valueOf(post.getUpdatedAt()));
    }

    private String convertTagsToJson(List<String> tags) {
        try {
            return objectMapper.writeValueAsString(tags);
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<String> convertJsonToTags(String tagsJson) {
        try {
            if (tagsJson == null || tagsJson.isEmpty()) return List.of();
            return objectMapper.readValue(tagsJson, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
