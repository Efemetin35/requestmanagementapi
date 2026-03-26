package com.efe.requestmanagementapi.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(type = "string", allowableValues = {"Low", "Medium", "High"})
public enum RequestPriority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String apiValue;

    RequestPriority(String apiValue) {
        this.apiValue = apiValue;
    }

    @JsonValue
    public String getApiValue() {
        return apiValue;
    }

    @JsonCreator
    public static RequestPriority fromValue(String value) {
        for (RequestPriority priority : values()) {
            if (priority.apiValue.equalsIgnoreCase(value) || priority.name().equalsIgnoreCase(value)) {
                return priority;
            }
        }

        throw new IllegalArgumentException("Invalid priority value: " + value);
    }

    @Override
    public String toString() {
        return apiValue;
    }
}