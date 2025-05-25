package com.amulya.geo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)  //Can include as many properties as per requirement, currently only using these.
public record Properties(
        @JsonProperty("name") String name,
        @JsonProperty("stroke") String stroke,
        @JsonProperty("stroke-width") Integer strokeWidth,
        @JsonProperty("stroke-opacity") Integer strokeOpacity,
        @JsonProperty("fill") String fill
) {
}