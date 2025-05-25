package com.amulya.geo.utility;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

public class GeometryDeserializerTest {
    private ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Geometry.class, new GeometryDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

    @Test
    void testDeserializePointJson() throws Exception {
        String json = "{\"type\":\"Point\",\"coordinates\":[3.0,4.0]}";
        ObjectMapper mapper = createMapper();
        Geometry geom = mapper.readValue(json, Geometry.class);
        assertEquals(3.0, geom.getCoordinate().x, 1e-9);
        assertEquals(4.0, geom.getCoordinate().y, 1e-9);
    }

    @Test
    void testDeserializeLineStringJson() throws Exception {
        String json = "{\"type\":\"LineString\",\"coordinates\":[[0,0],[1,1]]}";
        ObjectMapper mapper = createMapper();
        Geometry geom = mapper.readValue(json, Geometry.class);
        assertInstanceOf(LineString.class, geom);
        assertEquals(2, geom.getCoordinates().length);
    }

    @Test
    void testDeserializePolygonJson() throws Exception {
        String json = "{\"type\":\"Polygon\",\"coordinates\":[[[0,0],[1,0],[1,1],[0,1],[0,0]]]}";
        ObjectMapper mapper = createMapper();
        Geometry geom = mapper.readValue(json, Geometry.class);
        assertInstanceOf(Polygon.class, geom);
        assertEquals(5, geom.getCoordinates().length);
    }
}
