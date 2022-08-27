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
@Builder
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @Max(200)
    private String description;
    @Past
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private int countOfLike;
    @Builder.Default
    private Set<Long> usersIdsOfLikes = new HashSet<>();
    private Set<String> genre = new HashSet<>();
    private Rating rating;
}
