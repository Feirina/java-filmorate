package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.Mappers;

import java.util.List;
import java.util.Optional;

@Component
public class MpaDbStorage implements MpaDaoStorage {
    private final JdbcTemplate jdbcTemplate;

    private final Mappers mappers;

    public MpaDbStorage(JdbcTemplate jdbcTemplate, Mappers mappers) {
        this.jdbcTemplate = jdbcTemplate;
        this.mappers = mappers;
    }

    @Override
    public List<Mpa> getAll() {
        final String sql = "SELECT * FROM mpa";

        return jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeRating(rs));
    }

    @Override
    public Optional<Mpa> getById(Long id) {
        final String sql = "SELECT * FROM mpa WHERE mpa_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> mappers.makeRating(rs), id)
                .stream()
                .findAny();
    }
}
