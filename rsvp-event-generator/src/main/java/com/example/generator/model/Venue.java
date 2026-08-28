package com.example.generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Venue(
    @JsonProperty("venue_name") String venueName,
    double lat,
    double lon
) {}
