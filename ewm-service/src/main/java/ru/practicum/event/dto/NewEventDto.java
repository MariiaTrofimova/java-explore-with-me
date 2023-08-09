package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class NewEventDto {
    @Size(min = 20, max = 2000, message = "Размер аннотации должен быть от 20 до 2000 символов")
    private String annotation;
    @NotNull
    private Long category;
    @Size(min = 20, max = 7000, message = "Размер описания должен быть от 20 до 7000 символов")
    private String description;
    @NotNull
    @FutureOrPresent(message = "Поле должно содержать дату, которая еще не наступила")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private LocationDto location;
    //default: false
    private boolean paid;
    //default: 0
    private int participantLimit;
    //default true
    private Boolean requestModeration;
    @Size(min = 3, max = 120, message = "Заголовок должен быть от 3 до 120 символов")
    private String title;
}