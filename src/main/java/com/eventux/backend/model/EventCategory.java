package com.eventux.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EventCategory {
    Weddings("Weddings"),
    Birthdays("Birthdays"),
    Anniversaries("Anniversaries"),
    Business_Conferences("Business Conferences"),
    Meetings("Meetings"),
    Family_Gatherings("Family Gatherings"),
    OTHER("OTHER");

    private final String label;

    EventCategory(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static EventCategory fromValue(String value) {
        for (EventCategory c : values()) {
            if (c.label.equalsIgnoreCase(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown EventCategory: " + value);
    }
}
