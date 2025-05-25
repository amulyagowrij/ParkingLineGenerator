package com.amulya.geo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

//Finally put this in GeoJSON record recognized by geojson.io
public record GeoJsonRecord(
        @JsonProperty("type") String type,
        @JsonProperty("features") List<Feature> features
) {
}