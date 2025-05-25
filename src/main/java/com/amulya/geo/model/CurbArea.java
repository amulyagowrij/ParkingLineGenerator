package com.amulya.geo.model;

import com.amulya.geo.utility.GeometryDeserializer;
import com.amulya.geo.utility.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.locationtech.jts.geom.Geometry;

import java.util.Set;

//Curb Area Record
public record CurbArea(
        @JsonProperty("curb_area_id") Long curbAreaId,
        //Using JsonDeserialize to avoid deserialization / serialization issues
        @JsonProperty("geometry") @JsonSerialize(using = GeometrySerializer.class) @JsonDeserialize(using = GeometryDeserializer.class) Geometry geometry,
        @JsonProperty("published_date") Long publishedDate,
        @JsonProperty("last_updated_date") Long lastUpdatedDate,
        @JsonProperty("curb_zone_ids") Set<String> curbZoneIds
) {
}