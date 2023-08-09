package ru.practicum.request.mapper;

import ru.practicum.request.Request;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.util.DateTime;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(DateTime.toLocalDateTime(request.getCreated()))
                .event(request.getEventId())
                .requester(request.getRequesterId())
                .build();
    }

    public static List<ParticipationRequestDto> toParticipationRequestDto(List<Request> requests) {
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    public static EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(List<Request> requests) {
        if (requests.isEmpty()) {
            return new EventRequestStatusUpdateResult(Collections.emptyList(), Collections.emptyList());
        }
        List<ParticipationRequestDto> confirmedRequests = requests.stream()
                .filter(request -> request.getStatus() == RequestStatus.CONFIRMED)
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
        List<ParticipationRequestDto> rejectedRequests = requests.stream()
                .filter(request -> request.getStatus() == RequestStatus.REJECTED)
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }
}