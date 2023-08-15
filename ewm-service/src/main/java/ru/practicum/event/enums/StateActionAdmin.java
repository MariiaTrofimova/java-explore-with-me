package ru.practicum.event.enums;

import java.util.Optional;

public enum StateActionAdmin {
    PUBLISH_EVENT,
    REJECT_EVENT;

    public static Optional<StateActionAdmin> from(String stringState) {
        for (StateActionAdmin stateAction : values()) {
            if (stateAction.name().equalsIgnoreCase(stringState)) {
                return Optional.of(stateAction);
            }
        }
        return Optional.empty();
    }
}
