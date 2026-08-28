package com.example.generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Event(
    @JsonProperty("event_id") String eventId,
    @JsonProperty("event_name") String eventName
) {}
