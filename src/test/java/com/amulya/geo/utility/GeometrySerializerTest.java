package com.amulya.geo.utility;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.locationtech.jts.geom.*;

public class GeometrySerializerTest {

    private ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Geometry.class, new GeometrySerializer());
        mapper.registerModule(module);
        return mapper;
    }

    @Test
    void testSerializePoint() throws Exception {
        GeometryFactory gf = new GeometryFactory();
        Point pt = gf.createPoint(new Coordinate(1.0, 2.0));

        ObjectMapper mapper = createMapper();
        String json = mapper.writeValueAsString(pt);

        JsonNode node = new ObjectMapper().readTree(json); // Parse output as JSON
        assertEquals("Point", node.get("type").asText());
        assertEquals(1.0, node.get("coordinates").get(0).asDouble(), 1e-9);
        assertEquals(2.0, node.get("coordinates").get(1).asDouble(), 1e-9);
    }


    @Test
    void testSerializeLineString() throws Exception {
        GeometryFactory gf = new GeometryFactory();
        LineString ls = gf.createLineString(new Coordinate[]{
                new Coordinate(0, 0),
                new Coordinate(1, 1)
        });

        ObjectMapper mapper = createMapper();
        String json = mapper.writeValueAsString(ls);

        JsonNode node = new ObjectMapper().readTree(json);
        assertEquals("LineString", node.get("type").asText());

        JsonNode coords = node.get("coordinates");
        assertEquals(2, coords.size());

        assertEquals(0.0, coords.get(0).get(0).asDouble(), 1e-9); // [0.0, 0.0]
        assertEquals(0.0, coords.get(0).get(1).asDouble(), 1e-9);

        assertEquals(1.0, coords.get(1).get(0).asDouble(), 1e-9); // [1.0, 1.0]
        assertEquals(1.0, coords.get(1).get(1).asDouble(), 1e-9);
    }


    @Test
    void testSerializePolygon() throws Exception {
        GeometryFactory gf = new GeometryFactory();
        Polygon poly = gf.createPolygon(new Coordinate[]{
                new Coordinate(0, 0),
                new Coordinate(1, 0),
                new Coordinate(1, 1),
                new Coordinate(0, 1),
                new Coordinate(0, 0)
        });

        ObjectMapper mapper = createMapper();
        String json = mapper.writeValueAsString(poly);

        JsonNode node = new ObjectMapper().readTree(json);
        assertEquals("Polygon", node.get("type").asText());

        JsonNode coords = node.get("coordinates");
        assertTrue(coords.isArray());
        assertTrue(coords.get(0).isArray());  // Outer ring

        JsonNode ring = coords.get(0);
        assertEquals(5, ring.size());

        assertEquals(0.0, ring.get(0).get(0).asDouble(), 1e-9);
        assertEquals(0.0, ring.get(0).get(1).asDouble(), 1e-9);

        assertEquals(1.0, ring.get(2).get(0).asDouble(), 1e-9);
        assertEquals(1.0, ring.get(2).get(1).asDouble(), 1e-9);
    }


}
