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
}
