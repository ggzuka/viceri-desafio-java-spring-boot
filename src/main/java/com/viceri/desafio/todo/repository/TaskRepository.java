package com.viceri.desafio.todo.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.viceri.desafio.todo.domain.enums.TaskPriority;
import com.viceri.desafio.todo.domain.model.Task;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Task> rowMapper = (rs, rowNum) -> {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setDescription(rs.getString("description"));
        task.setPriority(TaskPriority.valueOf(rs.getString("priority")));
        task.setCompleted(rs.getBoolean("completed"));
        task.setUserId(rs.getLong("user_id"));
        task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        task.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return task;
    };

    public TaskRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Task save(Task task) {
        String sql = "INSERT INTO tasks (description, priority, completed, user_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, task.getDescription());
            ps.setString(2, task.getPriority().name());
            ps.setBoolean(3, task.isCompleted());
            ps.setLong(4, task.getUserId());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);

        Long generatedId = getGeneratedId(keyHolder);
        if (generatedId != null) {
            task.setId(generatedId);
            task.setCreatedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
        }
        return task;
    }

    public boolean delete(Long id, Long userId) {
        String sql = "DELETE FROM tasks WHERE id = ? AND user_id = ?";
        int affectedRows = jdbcTemplate.update(sql, id, userId);
        return affectedRows > 0;
    }

    public Optional<Task> findById(Long id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        try {
            Task task = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(task);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Task> findPendingByUserIdAndPriority(Long userId, TaskPriority priority) {
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND priority = ? and completed = false ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper, userId, priority.name());
    }

    public List<Task> findPendingByUserId(Long userId) {
        String sql = "SELECT * FROM tasks WHERE user_id = ? AND completed = false ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    public Optional<Task> update(Long id, String description, TaskPriority priority, Long userId) {
        String sql = "UPDATE tasks SET description = ?, priority = ?, updated_at = ? WHERE id = ? AND user_id = ?";
        int affectedRows = jdbcTemplate.update(sql,
                description,
                priority.name(),
                Timestamp.valueOf(LocalDateTime.now()),
                id,
                userId);
        return affectedRows > 0 ? findById(id) : Optional.empty();
    }

    public Optional<Task> markTaskAsCompleted(Long id, Long userId) {
        String sql = "UPDATE tasks SET completed = true, updated_at = ? WHERE id = ? AND user_id = ?";
        int affectedRows = jdbcTemplate.update(sql,
                Timestamp.valueOf(LocalDateTime.now()),
                id,
                userId);
        return affectedRows > 0 ? findById(id) : Optional.empty();
    }

    private Long getGeneratedId(KeyHolder keyHolder) {
        try {
            return (Long) keyHolder.getKeys().get("id");
        } catch (Exception e) {
            System.err.println("Erro ao obter ID gerado: " + e.getMessage());
        }
        return null;
    }
}