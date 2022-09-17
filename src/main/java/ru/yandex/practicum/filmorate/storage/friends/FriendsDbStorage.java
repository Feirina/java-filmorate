package ru.yandex.practicum.filmorate.storage.friends;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Mappers;

import java.util.List;

@Component
public class FriendsDbStorage implements FriendsDaoStorage{
    private final JdbcTemplate jdbcTemplate;
    private final Mappers mappers;

    public FriendsDbStorage(JdbcTemplate jdbcTemplate, Mappers mappers) {
        this.jdbcTemplate = jdbcTemplate;
        this.mappers = mappers;
    }

    @Override
    public List<User> getListOfFriends(Long userId) {
        final String sql = "SELECT * FROM filmorate_user WHERE id IN (SELECT friend_id FROM friend_list WHERE user_id = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeUser(rs), userId);
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
        return jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeUser(rs), userId, otherId);
    }
}
