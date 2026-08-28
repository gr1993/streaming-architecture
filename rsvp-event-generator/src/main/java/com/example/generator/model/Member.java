package com.example.generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Member(
    @JsonProperty("member_id") long memberId,
    @JsonProperty("member_name") String memberName
) {}
