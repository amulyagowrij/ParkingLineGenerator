package com.amulya.geo.utility;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class UtilMethodsTest {

    @Test
    void testWriteFile() throws Exception {
        Path tempFile = Files.createTempFile("test", ".json");
        Map<String, String> map = Map.of("foo", "bar");
        UtilMethods.writeFile(map, tempFile);
        String content = Files.readString(tempFile);
        assertTrue(content.contains("\"foo\""));
        assertTrue(content.contains("\"bar\""));
    }

    @Test
    void testIsNullString() {
        assertTrue(UtilMethods.isNull((String) null));
        assertFalse(UtilMethods.isNull("hello"));
    }

    @Test
    void testIsNullBoolean() {
        assertTrue(UtilMethods.isNull((Boolean) null));
        assertFalse(UtilMethods.isNull(Boolean.TRUE));
    }

    @Test
    void testIsNullNumber() {
        assertTrue(UtilMethods.isNull((Number) null));
        assertFalse(UtilMethods.isNull(123));
    }

    @Test
    void testIsNullCollection() {
        assertTrue(UtilMethods.isNull((java.util.Collection<?>) null));
        assertFalse(UtilMethods.isNull(Collections.emptyList()));
    }

    @Test
    void testIsNullMap() {
        assertTrue(UtilMethods.isNull((java.util.Map<?, ?>) null));
        assertFalse(UtilMethods.isNull(Map.of()));
    }

    @Test
    void testIsNullObject() {
        assertTrue(UtilMethods.isNull((Object) null));
        assertFalse(UtilMethods.isNull(new Object()));
    }

    @Test
    void testGetOffsetDistanceBasic() {
        double perpRight = UtilMethods.getOffsetDistance("right", "PERPENDICULAR");
        assertEquals(3.5 / 111000.0, perpRight, 1e-9);

        double diagLeft = UtilMethods.getOffsetDistance("LEFT", "angled");
        assertEquals(-3.0 / 111000.0, diagLeft, 1e-9);

        double parallelRight = UtilMethods.getOffsetDistance("RiGhT", "parallel");
        assertEquals(2.5 / 111000.0, parallelRight, 1e-9);

        double defaultVal = UtilMethods.getOffsetDistance("left", "unknownAngle");
        assertEquals(0.0, defaultVal, 1e-9);
    }

}
