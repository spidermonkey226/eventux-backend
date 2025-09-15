package com.eventux.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum City {
    TEL_AVIV("Tel Aviv"),
    JERUSALEM("Jerusalem"),
    HAIFA("Haifa"),
    BEER_SHEVA("Be'er Sheva"),
    EILAT("Eilat"),
    NAZARETH("Nazareth"),
    NETANYA("Netanya"),
    HOLON("Holon"),
    ASHDOD("Ashdod"),
    RISHON_LEZION("Rishon LeZion");

    private final String value;

    City(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @JsonCreator
    public static City fromJson(String value) {
        // Try enum name first (TEL_AVIV), then fallback to label ("Tel Aviv")
        for (City c : City.values()) {
            if (c.name().equalsIgnoreCase(value) || c.value.equalsIgnoreCase(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Invalid city: " + value);
    }

}
