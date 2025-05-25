package com.amulya.geo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LocationReference(
        @JsonProperty("source") String source,
        @JsonProperty("ref_id") String refId,
        @JsonProperty("start") Integer start,
        @JsonProperty("end") Integer end,
        @JsonProperty("side") String side
) {
}