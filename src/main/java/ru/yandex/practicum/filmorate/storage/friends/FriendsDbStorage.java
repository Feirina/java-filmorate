package ru.yandex.practicum.filmorate.storage.friends;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FriendsDbStorage implements FriendsDaoStorage{
    private final JdbcTemplate jdbcTemplate;

    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getListOfFriends(Long userId) {
        final String sql = "SELECT * FROM filmorate_user WHERE id IN (SELECT friend_id FROM friend_list WHERE user_id = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId);
    }

    @Override
    public void addToFriends(Long userId, Long friendId) {
        final String sql = "INSERT INTO friend_list(user_id, friend_id, status_of_friendship) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, false);
    }

    @Override
    public void removeFromFriends(Long userId, Long friendId) {
        final String sql = "DELETE FROM friend_list WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getListOfMutualFriends(Long userId, Long otherId) {
        final String sql = "SELECT * FROM filmorate_user WHERE id IN (SELECT friend_id FROM friend_list " +
                "WHERE user_id = ? AND friend_id IN " +
                "(SELECT friend_id FROM friend_list WHERE user_id = ?))";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId, otherId);
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
}
