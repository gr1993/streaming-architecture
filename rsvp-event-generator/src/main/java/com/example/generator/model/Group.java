package com.example.generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Group(
    @JsonProperty("group_name") String groupName,
    @JsonProperty("group_city") String groupCity
) {}
