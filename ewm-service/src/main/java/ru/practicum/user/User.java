package ru.practicum.user;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class User {
    private long id;
    private String email;
    private String name;

    public Map<String, Object> toMap() {
        return Map.of(
                "name", name,
                "email", email
        );
    }
}
