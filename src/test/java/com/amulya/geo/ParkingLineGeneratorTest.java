package com.amulya.geo;

import com.amulya.geo.model.*;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amulya.geo.utility.Constants;
import org.locationtech.jts.geom.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParkingLineGeneratorTest {

    private ObjectMapper mapper;
    private GeometryFactory geometryFactory;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        geometryFactory = new GeometryFactory();
    }

    @Test
    public void testCreateParkingLineGeoJson_generatesOutput() throws Exception {
        // --- Setup simple mock data ---
        // Central area: a square
        Coordinate[] areaCoords = {
                new Coordinate(0,0), new Coordinate(0,10), new Coordinate(10,10), new Coordinate(10,0), new Coordinate(0,0)
        };
        Polygon areaPolygon = geometryFactory.createPolygon(areaCoords);

        CurbArea curbArea = new CurbArea(
                12345L,
                areaPolygon,
                11111L,
                1111111L,
                Set.of("z1")
        );

        // CurbZone: a straight line on one edge
        Coordinate[] zoneCoords = { new Coordinate(0,0), new Coordinate(0,10) };
        LineString zoneLine = geometryFactory.createLineString(zoneCoords);

        LocationReference locRef = new LocationReference("src", "ref", 0, 0, "RIGHT");
        CurbZone curbZone = new CurbZone(
                "z1",
                zoneLine,
                1111111L,
                2222222L,
                3333333L,
                List.of(locRef),
                "SomeStreet",
                "CrossStart",
                "CrossEnd",
                List.of(),
                "PARALLEL",
                5
        );

        Map<String, CurbZone> curbZoneMap = Map.of("z1", curbZone);

        // Ensure the output directory exists
        Files.createDirectories(Path.of("target/test-output"));
        // Use reflection or make createParkingLineGeoJson package-private for test
        ParkingLineGenerator.createParkingLineGeoJson(curbZoneMap, curbArea);

        // --- Validate output ---
        File outputFile = new File( "src/main/resources/output_" + curbArea.curbAreaId()+".json");
        assertTrue(outputFile.exists(), "GeoJson output file should be created");

        // Optionally, parse output and verify some properties
        // (here: just ensure the file is not empty)
        String content = Files.readString(outputFile.toPath());
        assertTrue(content.contains("Parking Line"), "Output should mention 'Parking Line'");
        assertTrue(content.contains("Curb Area"), "Output should mention 'Curb Area'");


        outputFile.delete();
    }


    @Test
    void testCurbAreaRecordDeserialization() throws Exception {

        File file = new File("src/main/resources/areas.json");
        CurbAreaRecord record = mapper.readValue(file, CurbAreaRecord.class);
        assertNotNull(record);
        List<CurbArea> areas = record.areas();
        assertFalse(areas.isEmpty());
        CurbArea area = areas.get(0);
        assertEquals(51005034L, area.curbAreaId());
        assertNotNull(area.geometry());
        assertInstanceOf(Polygon.class, area.geometry());
        Set<String> ids = area.curbZoneIds();
        assertEquals(6, ids.size());
        assertTrue(ids.contains("fe66b357-9e0e-4056-aede-663d4c82f24a"));
        assertEquals(1565897090L, area.publishedDate());
        assertEquals(1720555496L, area.lastUpdatedDate());
    }

    @Test
    void testCurbZonesRecordDeserialization() throws Exception {

        File file = new File("src/main/resources/zones.json");
        CurbZonesRecord record = mapper.readValue(file, CurbZonesRecord.class);
        assertNotNull(record);
        List<CurbZone> zones = record.zones();
        assertFalse(zones.isEmpty());
        CurbZone zone = zones.get(0);
        assertEquals("fe66b357-9e0e-4056-aede-663d4c82f24a", zone.curbZoneId());
        assertNotNull(zone.geometry());
        assertInstanceOf(LineString.class, zone.geometry());
        assertEquals(1570474612L, zone.publishedDate());
        assertEquals(1570474612L, zone.lastUpdatedDate());
        assertEquals(1570474612L, zone.startDate());
        List<LocationReference> refs = zone.locationReferences();
        assertFalse(refs.isEmpty());
        LocationReference ref = refs.get(0);
        assertEquals("http://openlr.org", ref.source());
        assertEquals("CCgBEAAkI6jmwRrVhgAJBQQEA30ACgQDBGYAAAb/pAAJBQQEA/0AMFcJ", ref.refId());
        assertEquals(8700, ref.start());
        assertEquals(700, ref.end());
        assertEquals("left", ref.side());
        assertNull(zone.parkingAngle());
        assertEquals(0, zone.numSpaces());
    }

    @Test
    void testGeoJsonRecordAndFeatureSerialization() throws Exception {

        GeometryFactory gf = new GeometryFactory();
        org.locationtech.jts.geom.Point pt = gf.createPoint(new org.locationtech.jts.geom.Coordinate(1, 2));
        Properties props = new Properties("road", "#00ff00", 3, null, null);
        Feature feature = new Feature("Feature", props, pt);
        GeoJsonRecord record = new GeoJsonRecord("FeatureCollection", List.of(feature));
        String json = mapper.writeValueAsString(record);
        assertTrue(json.contains("FeatureCollection"));
        assertTrue(json.contains("Feature"));
        assertTrue(json.contains("coordinates"));
    }

    @Test
    void testJsonRoundTripAreas() throws Exception {

        JsonNode original = mapper.readTree(new File("src/main/resources/areas.json"));
        CurbAreaRecord record = mapper.treeToValue(original, CurbAreaRecord.class);
        JsonNode roundTripped = mapper.valueToTree(record);

        JsonNode originalAreas = original.path("areas"); // Use the correct key
        JsonNode roundTrippedAreas = roundTripped.path("areas");

        assertTrue(originalAreas.isArray(), "Original 'areas' node is not an array");
        assertTrue(roundTrippedAreas.isArray(), "Round-tripped 'areas' node is not an array");

        assertEquals(originalAreas.size(), roundTrippedAreas.size());
    }


    @Test
    void testJsonRoundTripZones() throws Exception {

        JsonNode original = mapper.readTree(new File("src/main/resources/zones.json"));
        CurbZonesRecord record = mapper.treeToValue(original, CurbZonesRecord.class);
        JsonNode roundTripped = mapper.valueToTree(record);
        assertEquals(original.get("zones").size(), roundTripped.get("zones").size());
    }

    @Test
    void testDeserializeMissingOptionalFields() {
        String json = "{\"type\":\"Feature\"}";

        assertDoesNotThrow(() -> {
            Feature f = mapper.readValue(json, Feature.class);
            assertNull(f.properties());
            assertNull(f.geometry());
        });
    }
}
