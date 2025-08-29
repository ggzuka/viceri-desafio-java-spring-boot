package com.viceri.desafio.todo.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.viceri.desafio.todo.domain.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> rowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    };

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User save(User user) {
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            return ps;
        }, keyHolder);

        Long generatedId = getGeneratedId(keyHolder);
        if (generatedId != null) {
            user.setId(generatedId);
        }
        return user;
    }

    private Long getGeneratedId(KeyHolder keyHolder) {
        try {
            return (Long) keyHolder.getKeys().get("id");
        } catch (Exception e) {
            System.err.println("Erro ao obter ID gerado: " + e.getMessage());
        }
        return null;
    }

    public Optional<User> findByEmail(String email) {
        var sql = "SELECT id, name, email, password, created_at FROM users WHERE email = ?";
        var list = jdbcTemplate.query(sql, rowMapper, email);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Optional<User> findById(Long id) {
        var sql = "SELECT id, name, email, password, created_at FROM users WHERE id = ?";
        var list = jdbcTemplate.query(sql, rowMapper, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
