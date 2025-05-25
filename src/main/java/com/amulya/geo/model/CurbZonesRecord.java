package com.amulya.geo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

//Wrapper for Curb Zones
public record CurbZonesRecord(
        @JsonProperty("zones") List<CurbZone> zones
) { }
