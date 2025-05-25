package com.amulya.geo.model;


import com.amulya.geo.utility.GeometryDeserializer;
import com.amulya.geo.utility.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.locationtech.jts.geom.Geometry;

import java.util.List;

//Curb Zone Record
public record CurbZone(
        @JsonProperty("curb_zone_id") String curbZoneId,
        //Using JsonDeserialize to avoid deserialization / serialization issues
        @JsonProperty("geometry") @JsonSerialize(using = GeometrySerializer.class) @JsonDeserialize(using = GeometryDeserializer.class) Geometry geometry,
        @JsonProperty("published_date") Long publishedDate,
        @JsonProperty("last_updated_date") Long lastUpdatedDate,
        @JsonProperty("start_date") Long startDate,
        @JsonProperty("location_references") List<LocationReference> locationReferences,
        @JsonProperty("street_name") String streetName,
        @JsonProperty("cross_street_start_name") String crossStreetStartName,
        @JsonProperty("cross_street_end_name") String crossStreetEndName,
        @JsonProperty("curb_policy_ids") List<String> curbPolicyIds,
        @JsonProperty("parking_angle") String parkingAngle,
        @JsonProperty("num_spaces") Integer numSpaces
) {
}