package ru.practicum.event.enums;

import java.util.Optional;

public enum StateActionPrivate {
    SEND_TO_REVIEW,
    CANCEL_REVIEW;

    public static Optional<StateActionPrivate> from(String stringState) {
        for (StateActionPrivate stateAction : values()) {
            if (stateAction.name().equalsIgnoreCase(stringState)) {
                return Optional.of(stateAction);
            }
        }
        return Optional.empty();
    }
}
