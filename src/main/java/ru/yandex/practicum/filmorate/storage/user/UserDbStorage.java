package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Mappers;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Component("UserDbStorage")
public class UserDbStorage implements UserStorage{
    private final JdbcTemplate jdbcTemplate;

    private final Mappers mappers;

    public UserDbStorage(JdbcTemplate jdbcTemplate, Mappers mappers) {
        this.jdbcTemplate = jdbcTemplate;
        this.mappers = mappers;
    }

    @Override
    public List<User> getAll() {
        final String sql = "SELECT * FROM filmorate_user";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeUser(rs));
    }

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "INSERT INTO filmorate_user (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"ID"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public void delete(Long id) {
        final String sql = "DELETE FROM filmorate_user WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public User update(User user) {
        final String sql = "UPDATE filmorate_user SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public Optional<User> getById(Long id) {
        final String sql = "SELECT * FROM filmorate_user WHERE id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeUser(rs), id)
                .stream()
                .findAny();
    }
}
