package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Mappers;

import java.util.List;
import java.util.Optional;

@Component
public class GenreDbStorage implements GenreDaoStorage {
    private final JdbcTemplate jdbcTemplate;

    private final Mappers mappers;

    public GenreDbStorage(JdbcTemplate jdbcTemplate, Mappers mappers) {
        this.jdbcTemplate = jdbcTemplate;
        this.mappers = mappers;
    }

    @Override
    public List<Genre> getAll() {
        final String sql = "SELECT * FROM genre ORDER BY id";

        return jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeGenre(rs));
    }

    @Override
    public Optional<Genre> getById(Long id) {
        final String sql = "SELECT * FROM genre WHERE id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeGenre(rs), id)
                .stream()
                .findAny();
    }
}
