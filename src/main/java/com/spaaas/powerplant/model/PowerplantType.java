package com.spaaas.powerplant.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PowerplantType {
    GASFIRED("gasfired"),
    TURBOJET("turbojet"),
    WINDTURBINE("windturbine");

    private final String value;

    PowerplantType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static PowerplantType fromValue(String value) {
        for (PowerplantType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown powerplant type: " + value);
    }
}
