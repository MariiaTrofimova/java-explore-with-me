package ru.practicum.event.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.event.enums.EventState;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Size;
import java.time.Instant;

@Data
@Builder
public class Event {
    long id;
    @Size(min = 20, max = 2000, message = "Размер аннотации должен быть от 20 до 2000 символов")
    private String annotation;
    private long categoryId;
    @Size(min = 20, max = 7000, message = "Размер описания должен быть от 20 до 7000 символов")
    private String description;
    @FutureOrPresent(message = "Поле должно содержать дату, которая еще не наступила")
    private Instant eventDate;
    private long locationId;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    @Size(min = 3, max = 120, message = "Заголовок должен быть от 3 до 120 символов")
    private String title;
    private long initiator;
    private Instant createdOn;
    private Instant publishedOn;
    private EventState eventState;
}