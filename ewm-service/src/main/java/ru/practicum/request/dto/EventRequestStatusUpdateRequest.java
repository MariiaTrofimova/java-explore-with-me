package ru.practicum.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.request.enums.RequestStatus;

import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateRequest {
    private RequestStatus status;
    private List<Long> requestIds;
}