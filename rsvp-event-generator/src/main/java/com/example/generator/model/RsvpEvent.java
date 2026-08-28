package com.example.generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RsvpEvent(
    Member member,
    Event event,
    Group group,
    Venue venue,
    String response,
    int guests,
    @JsonProperty("rsvp_id") long rsvpId,
    long mtime
) {}
