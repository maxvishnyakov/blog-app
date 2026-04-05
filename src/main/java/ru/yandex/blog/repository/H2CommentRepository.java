package ru.yandex.blog.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.blog.domain.Comment;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class H2CommentRepository implements CommentRepository {
    private final JdbcTemplate jdbcTemplate;

    public H2CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Comment> commentRowMapper = (resultSet, row) -> {
        Long id = resultSet.getLong("id");
        Long post_id = resultSet.getLong("post_id");
        String content = resultSet.getString("content");
        LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updatedAt = resultSet.getTimestamp("updated_at").toLocalDateTime();
        return new Comment(id, post_id, content, createdAt, updatedAt);
    };

    @Override
    public Comment create(Comment comment) {
        String sql = """
                INSERT INTO comments (post_id, content, created_at, updated_at
                VALUES (?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, comment.getPostId());
            statement.setString(2, comment.getContent());
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            return statement;
        });
        comment.setId(keyHolder.getKey().longValue());
        return comment;
    }

    @Override
    public List<Comment> findByPostId(long postId) {
        String sql = "SELECT * FROM comments WHERE post_id = ? ORDER BY created_at ASC";
        List<Comment> comments = new ArrayList<>();
        jdbcTemplate.query(sql, rs -> {
            Comment comment = new Comment(rs.getLong("id"), rs.getLong("post_id"),
                    rs.getString("content"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime());
            comments.add(comment);
        }, postId);
        return comments;
    }

    @Override
    public Comment findByIdAndPostId(long id, long postId) {
        String sql = "SELECT FROM comments WHERE id = ? AND post_id = ?";
        return jdbcTemplate.queryForObject(sql, commentRowMapper, id, postId);
    }

    @Override
    public void update(Comment comment) {
        String sql = """
                UPDATE comments SET content = ?, updated_at = ?
                "WHERE id = ? AND post_id = ?
                """;
        jdbcTemplate.update(sql, comment.getContent(), Timestamp.valueOf(LocalDateTime.now()), comment.getId(),
                comment.getPostId());
    }

    @Override
    public void deleteByIdAndPostId(long id, long postId) {
        String sql = "DELETE FROM comments WHERE id = ? AND post_id = ?";
        jdbcTemplate.update(sql, commentRowMapper, id, postId);
    }

    @Override
    public void deleteByPostId(Long postId) {
        jdbcTemplate.update("DELETE FROM comments WHERE post_id = ?", postId);
    }
}
