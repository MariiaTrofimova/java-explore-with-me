package ru.practicum.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class EndpointHitDto {
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @NotBlank
    private String ip;
    @NotBlank
    private String timestamp;

    //"app": "ewm-main-service",
    //  "uri": "/events/1",
    //  "ip": "192.163.0.1",
    //  "timestamp": "2022-09-06 11:00:23"
}
