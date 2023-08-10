package ru.practicum.request.mapper;

import ru.practicum.request.Request;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.util.DateTime.toLocalDateTime;

public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(toLocalDateTime(request.getCreated()))
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

    public static EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(List<Request> confirmedReq,
                                                                                  List<Request> rejectedReq) {
        List<ParticipationRequestDto> confirmedRequests;
        List<ParticipationRequestDto> rejectedRequests;
        if (confirmedReq.isEmpty()) {
            confirmedRequests = Collections.emptyList();
        } else {
            confirmedRequests = confirmedReq.stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());
        }
        if (rejectedReq.isEmpty()) {
            rejectedRequests = Collections.emptyList();
        } else {
            rejectedRequests = rejectedReq.stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }
}