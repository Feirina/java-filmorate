package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component("UserDbStorage")
public class UserDbStorage implements UserStorage{
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAll() {
        final String sql = "SELECT * FROM filmorate_user";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User createUser(User user) {
        validation(user);
        if (user == null)
            throw new NotFoundException("Невозможно создать пользователя. Передано пустое значение пользователя.");
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
    public void deleteUser(Long id) {
        final String sql = "DELETE FROM filmorate_user WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public User updateUser(User user) {
        validation(user);
        final String sql = "UPDATE filmorate_user SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User getUser(Long id) {
        final String sql = "SELECT * FROM filmorate_user WHERE id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id)
                .stream()
                .findAny().orElse(null);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    public void validation(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("При создании или обновлении пользователя возникла ошибка при вводе e-mail");
            throw new ValidationException("Введен неккоректный e-mail адрес");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("При создании или обновлении пользователя возникла ошибка при вводе логина");
            throw new ValidationException("Введен неккоректный логин");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("При создании или обновлении пользователя возникла ошибка при вводе даты рождения");
            throw new ValidationException("Введена неккоректная дата рождения");
        }
    }
}
