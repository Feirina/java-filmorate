package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class Film {
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Размер описания фильма не может превышать 200 символов")
    private String description;

    @Positive(message = "Продолжительность фильма не может быть отрицательным значением")
    private int duration;

    private LocalDate releaseDate;

    private int countOfLike;

    @Builder.Default
    private Set<Long> usersIdsOfLikes = new HashSet<>();

    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    private Mpa mpa;
    
    @Builder.Default
    private Set<Director> directors = new HashSet<>();
}
