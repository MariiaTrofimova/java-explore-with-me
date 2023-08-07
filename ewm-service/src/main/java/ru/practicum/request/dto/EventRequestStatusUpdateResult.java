package ru.practicum.request.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateResult {
    private final List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
    private final List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
}
