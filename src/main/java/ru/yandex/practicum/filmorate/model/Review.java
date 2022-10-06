package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    private Long reviewId;
    @NotBlank
    private String content;
    @NotBlank
    private Boolean isPositive;
    @NotBlank
    private Long userId;
    @NotBlank
    private Long filmId;
    private Long useful;
}
