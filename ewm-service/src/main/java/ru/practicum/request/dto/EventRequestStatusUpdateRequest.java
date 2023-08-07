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

//Изменение статуса запроса на участие в событии текущего пользователя

//requestIds
//Идентификаторы запросов на участие в событии текущего пользователя

//status
//Новый статус запроса на участие в событии текущего пользователя
//[ CONFIRMED, REJECTED ]
