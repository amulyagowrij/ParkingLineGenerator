package com.amulya.geo.model;


import com.amulya.geo.utility.GeometryDeserializer;
import com.amulya.geo.utility.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.locationtech.jts.geom.Geometry;

public record Feature(
        @JsonProperty("type") String type,
        @JsonProperty("properties") Properties properties,
        @JsonProperty("geometry") @JsonSerialize(using = GeometrySerializer.class) @JsonDeserialize(using = GeometryDeserializer.class) Geometry geometry
) {
}
