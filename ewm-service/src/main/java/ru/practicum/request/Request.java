package ru.practicum.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.request.enums.RequestStatus;

import java.time.Instant;

@Data
@Builder
public class Request {
    private long id;
    private long eventId;
    private long requesterId;
    private Instant created;
    private RequestStatus status;
}