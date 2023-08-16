package ru.practicum.location.enums;

import java.util.Optional;

public enum LocationType {
    INDOOR,
    OUTDOOR,
    USERS_POINT;

    public static Optional<LocationType> from(String stringType) {
        for (LocationType type : values()) {
            if (type.name().equalsIgnoreCase(stringType)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}