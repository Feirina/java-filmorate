package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private int id = 1;
    @NotBlank
    private String name;
    @Max(200)
    private String description;
    @Past
    private LocalDate releaseDate;
    @Positive
    private int duration;
}
