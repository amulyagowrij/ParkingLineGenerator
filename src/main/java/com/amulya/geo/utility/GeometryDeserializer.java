package com.amulya.geo.utility;

/*

        The purpose of this class is to
        Avoid deserialization because of geometry.
        Making Sure Jackson dependency is able to deserialize Geometry without facing issue

*/

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.geojson.GeoJsonReader;

public class GeometryDeserializer extends JsonDeserializer<Geometry> {
    private final GeoJsonReader reader = new GeoJsonReader();

    @Override
    public Geometry deserialize(JsonParser p, DeserializationContext ctxt) {
        try {
            JsonNode node = p.getCodec().readTree(p);
            return reader.read(node.toString());
        } catch (Exception e) {
            throw new RuntimeException("GeoJSON deserialization failed", e);
        }
    }
}
