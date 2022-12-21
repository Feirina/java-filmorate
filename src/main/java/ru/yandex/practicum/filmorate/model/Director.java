package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class Director {
    private Long id;

    @NotBlank(message = "Имя режиссера не может быть пустым")
    private String name;
}
