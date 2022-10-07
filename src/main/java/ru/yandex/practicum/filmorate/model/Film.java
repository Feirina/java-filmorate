package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder=true)
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @Max(200)
    private String description;
    @Positive
    private int duration;
    @Past
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
